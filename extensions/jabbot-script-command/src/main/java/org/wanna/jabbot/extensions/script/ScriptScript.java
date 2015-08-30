package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.messaging.body.XhtmlBodyPart;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.QuotedStringArgParser;
import com.sipstacks.script.ScriptParseException;
import com.sipstacks.script.OutputStream;
import java.util.List;

/**
 * @author tsearle <tsearle>
 * @since 2015-03-20
 */
public class ScriptScript implements Command {

	com.sipstacks.script.Command scriptCmd;
	String name;
	String author;

	public ScriptScript(String name, com.sipstacks.script.Command scriptCmd, String author) {
		this.name = name;
		this.scriptCmd = scriptCmd;
		this.author = author;
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
	public String getHelpMessage() { return "Midori Script command written by " + author + "\n" + "sub " + name + " \n" + scriptCmd.dump() ;}

	public Message process(CommandMessage message) {
		String argsString = message.getBody();

		List<String> args = getArgsParser().parse(argsString);
		OutputStream response;

		try {
			response = scriptCmd.exec(args);
		} catch (ScriptParseException e) {
			DefaultCommandMessage result = new DefaultCommandMessage();
			result.setBody(e.getMessage());
			return result;

		}

		DefaultCommandMessage result = new DefaultCommandMessage();
		result.setBody(response.getText());
		if (response.getHtml().length() > 0) {
			result.addBody(new XhtmlBodyPart(response.getHtml()));
		}
		return result;
	}

	public void reset() {
		scriptCmd.reset();
	}
}

