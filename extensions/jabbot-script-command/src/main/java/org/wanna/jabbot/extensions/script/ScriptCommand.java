package org.wanna.jabbot.extensions.script;

import com.sipstacks.script.ExternalFunction;
import com.sipstacks.script.Script;
import com.sipstacks.script.ScriptParseException;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.NullArgParser;

import java.io.StringReader;
import java.util.Map;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class ScriptCommand extends AbstractCommandAdapter  implements CommandFactoryAware {
	private CommandFactory commandFactory;
	private int loopLimit = -1;
	private int bufferLimit = -1;

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

		for(Command command : commandFactory.getAvailableCommands().values()){
			s.addExternalFunction(command.getCommandName(), new ExternalFunction() {
					
				private Command cmd;
				private String sender;
				public ExternalFunction init(Command command, String sender) {
					cmd = command;
					this.sender = sender;
					return this;
				}

				public String run(String args) {
					DefaultCommandMessage msg = new DefaultCommandMessage();
					msg.setBody(args);
					msg.setSender(sender);
					CommandMessage result = cmd.process(msg);
					return result.getBody();
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
