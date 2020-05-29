package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public CommandParsingResult parse(String message) {
		String command;
		Pattern wsPattern = Pattern.compile("\\s");
		Matcher wsMatcher = wsPattern.matcher(message);
		if (wsMatcher.find()) {
			command = message.substring(commandPrefix.length(), wsMatcher.start());
		} else {
			command = message.substring(commandPrefix.length());
		}
		String argString = message.substring((commandPrefix + command).length());

		return new CommandParsingResult(command,argString);
	}
}
