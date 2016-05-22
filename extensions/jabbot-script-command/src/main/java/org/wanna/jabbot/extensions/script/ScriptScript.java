package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.MessageContent;
import org.wanna.jabbot.binding.messaging.body.XhtmlBodyPart;
import org.wanna.jabbot.command.*;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.ArgsParser;
import org.wanna.jabbot.command.parser.QuotedStringArgParser;
import com.sipstacks.script.ScriptParseException;
import com.sipstacks.script.ScriptFlowException;
import com.sipstacks.script.OutputStream;
import com.sipstacks.script.Function;
import com.sipstacks.script.StatementFunction;
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
			if(func instanceof StatementFunction) {
				StatementFunction stmtFunc = (StatementFunction)func;
				getFunctions(stmtFunc.getStatement(),sb);
				if (!(stmtFunc.getStatement() instanceof com.sipstacks.script.ExternalCommand)) {
					sb.append("local sub ");
					sb.append(stmtFunc.getName());
					sb.append(stmtFunc.getStatement().dump());
				}
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

	public MessageContent process(CommandMessage message) {
		List<String> args = getArgsParser().parse(message.getArgsLine());
		OutputStream response = new OutputStream();

		try {
			scriptCmd.exec(response,args);
		} catch (ScriptParseException | ScriptFlowException e) {
			return new DefaultMessageContent(e.getMessage());
		}

		MessageContent messageContent = new DefaultMessageContent(response.getText());
		if (response.getHtml().length() > 0) {
			messageContent.addBody(new XhtmlBodyPart(response.getHtml()));
		}
		return messageContent;
	}

	public void reset() {
		scriptCmd.reset();
	}
}

