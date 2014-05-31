package org.wanna.jabbot.command;

import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public interface CommandFactory {
	Command create(ParsedCommand parsedCommand);
	Map<String,Command> getAvailableCommands();
}
