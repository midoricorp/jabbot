package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.QuotedStringArgParser;
import com.sipstacks.script.ScriptParseException;
import java.util.List;

/**
 * @author tsearle <tsearle>
 * @since 2015-03-20
 */
public class ScriptScript implements Command {

	com.sipstacks.script.Command scriptCmd;
	String name;

	public ScriptScript(String name, com.sipstacks.script.Command scriptCmd) {
		this.name = name;
		this.scriptCmd = scriptCmd;
	}

        @Override
        public ArgsParser getArgsParser() {
                return new QuotedStringArgParser();
        }

	public String getCommandName() {
		return name;
	}

	/**
	 * Returns a short description of the command
	 * @return String command description
	 */
	public String getDescription() { return null;}

	/**
	 * Returns the command help
	 * @return String help message
	 */
	public String getHelpMessage() { return null;}

	public CommandMessage process(CommandMessage message) {
		String argsString = message.getBody();

		List<String> args = getArgsParser().parse(argsString);
		String response;

		try {
			response = scriptCmd.exec(args);
		} catch (ScriptParseException e) {
			DefaultCommandMessage result = new DefaultCommandMessage();
			result.setBody(e.getMessage());
			return result;

		}

		DefaultCommandMessage result = new DefaultCommandMessage();
		result.setBody(response);
		return result;
	}

	public void reset() {
		scriptCmd.reset();
	}
}

