package org.wanna.jabbot.extensions.script;

import com.sipstacks.script.ExternalCommand;
import com.sipstacks.script.Script;
import com.sipstacks.script.ScriptParseException;
import com.sipstacks.script.FunctionListener;
import com.sipstacks.script.OutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.NullArgParser;
import org.wanna.jabbot.command.parser.QuotedStringArgDeparser;
import org.wanna.jabbot.messaging.DefaultMessageContent;
import org.wanna.jabbot.messaging.DefaultResource;
import org.wanna.jabbot.messaging.MessageContent;
import org.wanna.jabbot.messaging.Resource;
import org.wanna.jabbot.messaging.body.*;

import java.io.*;
import java.util.Map;
import java.util.List;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class ScriptCommand extends AbstractCommandAdapter  implements CommandFactoryAware {
	final Logger logger = LoggerFactory.getLogger(ScriptCommand.class);
	private CommandFactory commandFactory;
	private int loopLimit = -1;
	private int bufferLimit = -1;
	private String scriptDir;

	private class ScriptFunctionListener implements FunctionListener {
		String author;
		boolean startup;

		public void addFunction(String name, com.sipstacks.script.Statement cmd) {

			Map<String,Command> cmds = commandFactory.getAvailableCommands();

			Command oldCmd = cmds.get(name);
			
			if (oldCmd != null && !(oldCmd instanceof ScriptScript)) {
				// don't nuke core commands!
				return;
			}

			ScriptScript ss = new ScriptScript(name, cmd, author);
			commandFactory.register(name,ss);

			if (!startup) {
				PrintWriter out = null;
				try {
					StringBuffer sb = new StringBuffer();
					out = new PrintWriter(scriptDir + File.separator + name + ".ss");
					ScriptScript.getFunctions(cmd, sb);
					sb.append("sub " + name + " \n" + cmd.dump());
					out.write(sb.toString());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
		}

		public ScriptFunctionListener init(String author, boolean startup) {
			this.author = author;
			this.startup = startup;
			return this;
		}
	}

	public ScriptCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;
		
		if (configuration.containsKey("loop_limit")) {
			loopLimit = Integer.parseInt(configuration.get("loop_limit").toString());
		}
		if (configuration.containsKey("buffer_limit")) {
			bufferLimit = Integer.parseInt(configuration.get("buffer_limit").toString());
		}
		if (configuration.containsKey("script_dir")) {
			scriptDir = configuration.get("script_dir").toString();
			File files = new File(scriptDir);
			if (!files.exists()) {
				files.mkdirs();
			}

			File[] listOfFiles = files.listFiles();
			for (File f : listOfFiles) {
				try {
					Script s = new Script(new FileReader(f));
					preloadFunctions(s, "Loaded from Disk", true);
					s.run();
				} catch (Exception e) {
					logger.error("Error loading script", e);
				}
			}
		}
	}

	@Override
	public ArgsParser getArgsParser() {
		return new NullArgParser();
	}

	@Override
	public MessageContent process(CommandMessage message) {
		String script = message.getArgsLine();

		Script s = new Script(new StringReader(script));
		if (loopLimit > 0) {
			s.setLoopLimit(loopLimit);
		}

		String address = message.getSender().getAddress();
		preloadFunctions(s, address, false);



		OutputStream response = null;
		MessageContent result = new DefaultMessageContent();

		try {
			response = s.run();
			String txt = response.getText();
			String html = response.getHtml();

			if (bufferLimit > 0 && txt.length() > bufferLimit) {
				txt = txt.substring(0, bufferLimit);
				txt += "\n*MessageContent Truncated*";
			}
			result.addBody(new TextBodyPart(txt));
			// can't safely truncate xhtml, so discard if too long
			if (bufferLimit > 0 && html.length() > bufferLimit) {
				html = "";
			}

			if (html.length() > 0) {
                BodyPart xhtmlPart = new XhtmlBodyPart(html);
                try {
                    BodyPartValidator validator = BodyPartValidatorFactory.getInstance().create(BodyPart.Type.XHTML);
                    if(validator != null){
                        validator.validate(xhtmlPart);
                    }
                    result.addBody(xhtmlPart);
                } catch (InvalidBodyPartException e) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(e.getMessage());
                    if(e.getInvalidBodyPart() != null){
                        errorMessage.append("<br/>");
                        errorMessage.append(StringEscapeUtils.escapeXml11(e.getInvalidBodyPart().getText()))
                                .append("<br/>")
                                .append(StringEscapeUtils.escapeXml11(e.getCause().getMessage()));

                    }
                    result.addBody(new XhtmlBodyPart(errorMessage.toString()));
                }
			}

		} catch (ScriptParseException spe) {
			result = new DefaultMessageContent();
			result.addBody(new TextBodyPart(spe.getMessage()));
			return result;
		}
		
		return result;
	}

	private void preloadFunctions(Script s, String address, boolean startup) {
		s.addFunctionListener(new ScriptFunctionListener().init(address, startup));

		for(Command command : commandFactory.getAvailableCommands().values()){
			// don't add yourself to limit recursion
			if (command.getCommandName().equals(getCommandName())) {
				continue;
			}

			if (command instanceof ScriptScript) {
				ScriptScript ss = (ScriptScript)command;
				s.addStatementFunction(ss.name, ss.scriptCmd);
			} else {

				s.addStatementFunction(command.getCommandName(), new ExternalCommand() {

					private Command cmd;
					private String sender;
					public ExternalCommand init(Command command, String sender) {
						cmd = command;
						this.sender = sender;
						return this;
					}

					public String run(List<String> args) {
						MessageContent content;

						String argsLine;
						if (args.size() > 0) {
							argsLine = QuotedStringArgDeparser.deparse(args);
						} else {
							argsLine = "";
						}
						Resource resource = new DefaultResource(sender,null);
						CommandMessage msg = new DefaultCommandMessage(resource,argsLine);
						MessageContent result = cmd.process(msg);
						return result.getBody();
					}

					public void reset() {
						if (cmd instanceof ScriptScript) {
							((ScriptScript)cmd).reset();
						}
					}

				}.init(command, address));
			}
		}
	}

	@Override
        public void setCommandFactory(CommandFactory commandFactory) {
                this.commandFactory = commandFactory;
        }


}
