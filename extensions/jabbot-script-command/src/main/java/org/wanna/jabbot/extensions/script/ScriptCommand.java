package org.wanna.jabbot.extensions.script;

import com.sipstacks.script.*;
import com.sipstacks.script.OutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
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
import java.util.List;
import java.util.Map;

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
	public ThreadLocal<Resource> currentUser = new ThreadLocal<>();

	private class DynamicResource implements Resource {

		@Override
		public String getAddress() {
			return currentUser.get().getAddress();
		}

		@Override
		public String getName() {
			return currentUser.get().getName();
		}

		@Override
		public Type getType() {
			return currentUser.get().getType();
		}
	}

	private class ScriptFunctionListener implements FunctionListener {
		boolean startup;

		public void addFunction(String name, com.sipstacks.script.Statement cmd) {

			Map<String,Command> cmds = commandFactory.getAvailableCommands();

			Command oldCmd = cmds.get(name);
			
			if (oldCmd != null && !(oldCmd instanceof ScriptScript)) {
				// don't nuke core commands!
				return;
			}

			ScriptScript ss = new ScriptScript(name, cmd, currentUser.get().getName(), ScriptCommand.this);
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

		public ScriptFunctionListener init(boolean startup) {
			this.startup = startup;
			return this;
		}
	}

	public ScriptCommand(String commandName) {
		super(commandName);
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;

		currentUser.set(new DefaultResource("Loaded From Disk", "Loaded From Disk"));

		if (configuration.containsKey("loop_limit")) {
			loopLimit = Integer.parseInt(configuration.get("loop_limit").toString());
		}
		if (configuration.containsKey("buffer_limit")) {
			bufferLimit = Integer.parseInt(configuration.get("buffer_limit").toString());
		}
		if (configuration.containsKey("script_dir")) {
			scriptDir = configuration.get("script_dir").toString();

		}
	}

	@Override
	public ArgsParser getArgsParser() {
		return new NullArgParser();
	}

	@Override
	public MessageContent process(CommandMessage message) {
		String script = message.getArgsLine();

		currentUser.set(message.getSender());

		Script s = new Script(new StringReader(script));
		if (loopLimit > 0) {
			s.setLoopLimit(loopLimit);
		}

		preloadFunctions(s, false);



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

	private void preloadFunctions(Script s, boolean startup) {
		s.addFunctionListener(new ScriptFunctionListener().init(startup));

		for(Command command : commandFactory.getAvailableCommands().values()){
			// don't add yourself to limit recursion
			if (command.getCommandName().equals(getCommandName())) {
				continue;
			}

			if (command instanceof ScriptScript) {
				// on startup scripts can conflict with variables
				// dependencies already included in file
				if (!startup) {
					ScriptScript ss = (ScriptScript) command;
					s.addStatementFunction(ss.name, ss.scriptCmd);
				}
			} else {

				s.addStatementFunction(command.getCommandName(), new ExternalCommand() {

					private Command cmd;
					public ExternalCommand init(Command command) {
						cmd = command;
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
						Resource resource = new DynamicResource();
						CommandMessage msg = new DefaultCommandMessage(resource,argsLine);
						MessageContent result = cmd.process(msg);
						return result.getBody();
					}

					public void reset() {
						if (cmd instanceof ScriptScript) {
							((ScriptScript)cmd).reset();
						}
					}

				}.init(command));
			}
		}
	}

	@Override
        public void setCommandFactory(CommandFactory commandFactory) {
                this.commandFactory = commandFactory;
        }

	@Override
	public void onCommandsLoaded() {
		if (scriptDir != null) {
			File files = new File(scriptDir);
			if (!files.exists()) {
				files.mkdirs();
			}

			File[] listOfFiles = files.listFiles();
			for (File f : listOfFiles) {
				try {
					Script s = new Script(new FileReader(f));
					preloadFunctions(s, true);
					s.run();
				} catch (Exception e) {
					logger.error("Error loading script", e);
				}
			}
		}

	}


}
