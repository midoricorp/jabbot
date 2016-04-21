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
import com.sipstacks.script.Function;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuffer;

/**
 * @author tsearle <tsearle>
 * @since 2015-03-20
 */
public class ScriptScript implements Command {

	com.sipstacks.script.Statement scriptCmd;
	String name;
	String author;

	public ScriptScript(String name, com.sipstacks.script.Statement scriptCmd, String author) {
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

	static void getFunctions(com.sipstacks.script.Statement cmd, StringBuffer sb) {
		List<Function> funcs = new ArrayList<Function>();
		cmd.getFunctions(funcs);
		for(Function func : funcs) {
			getFunctions(func.getStatement(),sb);
			if (!(func.getStatement() instanceof com.sipstacks.script.ExternalFunction)) {
				sb.append("local sub ");
				sb.append(func.getName());
				sb.append(func.getStatement().dump());
			}
		}
	}	

	/**
	 * Returns the command help
	 * @return String help message
	 */
	public String getHelpMessage() { 
		StringBuffer sb = new StringBuffer();
		sb.append("Midori Script command written by " + author + "\n");
		getFunctions(scriptCmd, sb);
		sb.append("sub " + name + " \n" + scriptCmd.dump());
		return sb.toString();
	}

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

