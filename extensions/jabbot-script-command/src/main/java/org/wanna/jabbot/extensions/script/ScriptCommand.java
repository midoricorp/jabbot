package org.wanna.jabbot.extensions.script;

import com.sipstacks.script.ExternalFunction;
import com.sipstacks.script.Script;
import com.sipstacks.script.ScriptParseException;
import com.sipstacks.script.FunctionListener;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
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
	public CommandMessage process(CommandMessage message) {
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
					DefaultCommandMessage msg = new DefaultCommandMessage();
					if (args.size() > 0) {
						msg.setBody(QuotedStringArgDeparser.deparse(args));
					} else {
						msg.setBody("");
					}

					msg.setSender(sender);
					CommandMessage result = cmd.process(msg);
					return result.getBody();
				}

				public void reset() {
					if (cmd instanceof ScriptScript) {
						((ScriptScript)cmd).reset();
					}
				}

			}.init(command, message.getSender()));
		}



		String response = null;

		try {
			response = s.run();

			if (bufferLimit > 0 && response.length() > bufferLimit) {
				response = response.substring(0, bufferLimit);
				response += "\n*Message Truncated*";
			}

		} catch (ScriptParseException spe) {
			DefaultCommandMessage result = new DefaultCommandMessage();
			result.setBody(spe.getMessage());
			return result;
		}
		
		DefaultCommandMessage result = new DefaultCommandMessage();
		result.setBody(response);
		return result;
	}

        @Override
        public void setCommandFactory(CommandFactory commandFactory) {
                this.commandFactory = commandFactory;
        }


}
