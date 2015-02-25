package org.wanna.jabbot.command.parser;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */

public interface CommandParser {
	CommandParsingResult parse(String message);
}

