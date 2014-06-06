package org.wanna.jabbot.command;

import java.util.Collection;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public interface CommandFactory {
	Command create(String commandName) throws CommandNotFoundException;
	Command create(ParsedCommand parsedCommand) throws CommandNotFoundException;
	Collection<Command> getAvailableCommands();

}
