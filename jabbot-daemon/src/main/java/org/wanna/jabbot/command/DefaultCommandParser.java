package org.wanna.jabbot.command;

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

	public ParsedCommand parse(String message) {
		ParsedCommand parsed = new ParsedCommand();

		String command;
		if (message.contains(" ")) {
			command = message.substring(commandPrefix.length(), message.indexOf(" "));
		} else {
			command = message.substring(commandPrefix.length());
		}

		parsed.setCommandName(command);
		StringTokenizer tokenizer = new StringTokenizer(message.substring((commandPrefix + command).length()), " ");
		List<String> args = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token = clearArgument(token);
			log.debug("one argument as been found '{}'", token);
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