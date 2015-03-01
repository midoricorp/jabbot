package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.parser.args.NullArgParser;
import org.wanna.jabbot.command.parser.args.ArgsParser;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;
import org.wanna.jabbot.command.CommandResult;
import org.wanna.jabbot.command.MessageWrapper;
import java.io.StringReader;


import com.sipstacks.script.*;


import java.util.Map;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class ScriptCommand extends AbstractCommandAdapter {
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
	public CommandResult process(MessageWrapper message) {
		String script = message.getArgs().get(0);

		Script s = new Script(new StringReader(script));
		if (loopLimit > 0) {
			s.setLoopLimit(loopLimit);
		}

		String response = null;

		try {
			response = s.run();

			if (bufferLimit > 0 && response.length() > bufferLimit) {
				response = response.substring(0, bufferLimit);
				response += "\n*Message Truncated*";
			}

		} catch (ScriptParseException spe) {
			CommandResult result = new CommandResult();
			result.setText(spe.getMessage());
			return result;
		}
		
		CommandResult result = new CommandResult();
		result.setText(response);
		return result;
	}

}
