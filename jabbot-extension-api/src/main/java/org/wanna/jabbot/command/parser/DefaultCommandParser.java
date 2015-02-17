package org.wanna.jabbot.command.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class DefaultCommandParser implements CommandParser {
	private Logger log = LoggerFactory.getLogger(DefaultCommandParser.class);
	private String commandPrefix = "!";

	public DefaultCommandParser(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	public ParsedCommand parse(String message) {
		ParsedCommand parsed = new ParsedCommand();

		String command;
		if (message.contains(" ")) {
			command = message.substring(commandPrefix.length(), message.indexOf(" "));
		} else {
			command = message.substring(commandPrefix.length());
		}

		parsed.setCommandName(command);
		String argString = message.substring((commandPrefix + command).length());
		List<String> args = new ArrayList<String>();
		boolean inQuote = false;
		boolean inToken = false;
		int start = 0;
		int end = 0;
		for (;end < argString.length();end++) {
			if (inQuote) {
				// if in a quoted string, token ends with endquote
				if (argString.charAt(end) == '"') {
					String token = argString.substring(start+1,end);
					args.add(token);
					inQuote = false;
					inToken = false;
				}
			} else if (!inToken) {
				// skip wite space until we hit the token start
				if(argString.charAt(end) != ' ' 
						&& argString.charAt(end) != '\t'
						&& argString.charAt(end) != '\r'
						&& argString.charAt(end) != '\n')
				{
					inToken = true;
					start = end;

					if (argString.charAt(end) == '"') {
						// woo this token is quoted
						// special rules apply!
						inQuote = true;
					}
				}
			} else {
				if(argString.charAt(end) == ' ' 
						|| argString.charAt(end) == '\t'
						|| argString.charAt(end) == '\r'
						|| argString.charAt(end) == '\n')
				{
					inToken = false;
					String token = argString.substring(start,end);
					args.add(token);
				}
			}
		}

		if (inToken) {
			// hit the end and it isn't whitespace! let's record the token
			inToken = false;
			String token = argString.substring(inQuote?start+1:start,end);
			args.add(token);
		}



		parsed.setArgs(args.toArray(new String[args.size()]));
		return parsed;
	}

	private String clearArgument(String arg) {
		arg = arg.trim();
		arg = arg.replaceAll("%", "%25");

		return arg;
	}

	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}
}
