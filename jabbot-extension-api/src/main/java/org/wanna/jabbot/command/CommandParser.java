package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */

public interface CommandParser {
	ParsedCommand parse(String message);
	String getCommandPrefix();
	void setCommandPrefix(String prefix);
}

