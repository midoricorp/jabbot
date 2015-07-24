package org.wanna.jabbot.extensions.script;

import com.sipstacks.script.ExternalFunction;
import com.sipstacks.script.Script;
import com.sipstacks.script.ScriptParseException;
import com.sipstacks.script.FunctionListener;
import com.sipstacks.script.OutputStream;
import org.apache.commons.lang3.StringEscapeUtils;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;
import org.wanna.jabbot.command.messaging.body.*;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.NullArgParser;
import org.wanna.jabbot.command.parser.QuotedStringArgDeparser;

import java.io.StringReader;
import java.util.Map;
import java.util.List;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class ScriptCommand extends AbstractCommandAdapter  implements CommandFactoryAware {
	private CommandFactory commandFactory;
	private int loopLimit = -1;
	private int bufferLimit = -1;

	private class ScriptFunctionListener implements FunctionListener {
		String author;

		public void addFunction(String name, com.sipstacks.script.Command cmd) {

			Map<String,Command> cmds = commandFactory.getAvailableCommands();

			Command oldCmd = cmds.get(name);
			
			if (oldCmd != null && !(oldCmd instanceof ScriptScript)) {
				// don't nuke core commands!
				return;
			}

			ScriptScript ss = new ScriptScript(name, cmd, author);
			commandFactory.register(name,ss);
		}

		public ScriptFunctionListener init(String author) {
			this.author = author;
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
	}

        @Override
        public ArgsParser getArgsParser() {
                return new NullArgParser();
        }

	@Override
	public Message process(Message message) {
		String script = message.getBody();

		Script s = new Script(new StringReader(script));
		if (loopLimit > 0) {
			s.setLoopLimit(loopLimit);
		}

		s.addFunctionListener(new ScriptFunctionListener().init(message.getSender()));

		for(Command command : commandFactory.getAvailableCommands().values()){
			// don't add yourself to limit recursion
			if (command.getCommandName().equals(getCommandName())) {
				continue;
			}

			s.addExternalFunction(command.getCommandName(), new ExternalFunction() {
					
				private Command cmd;
				private String sender;
				public ExternalFunction init(Command command, String sender) {
					cmd = command;
					this.sender = sender;
					return this;
				}

				public String run(List<String> args) {
					DefaultMessage msg = new DefaultMessage();
					if (args.size() > 0) {
						msg.setBody(QuotedStringArgDeparser.deparse(args));
					} else {
						msg.setBody("");
					}

					msg.setSender(sender);
					Message result = cmd.process(msg);
					return result.getBody();
				}

				public void reset() {
					if (cmd instanceof ScriptScript) {
						((ScriptScript)cmd).reset();
					}
				}

			}.init(command, message.getSender()));
		}



		OutputStream response = null;
		DefaultMessage result = new DefaultMessage();

		try {
			response = s.run();
			String txt = response.getText();
			String html = response.getHtml();

			if (bufferLimit > 0 && txt.length() > bufferLimit) {
				txt = txt.substring(0, bufferLimit);
				txt += "\n*Message Truncated*";
			}
			result.setBody(txt);
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
			result = new DefaultMessage();
			result.setBody(spe.getMessage());
			return result;
		}
		
		return result;
	}

        @Override
        public void setCommandFactory(CommandFactory commandFactory) {
                this.commandFactory = commandFactory;
        }


}
