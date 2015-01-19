package org.wanna.jabbot.command;

import org.wanna.jabbot.command.parser.ParsedCommand;

import java.util.Collection;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public interface CommandFactory {
	Command create(ParsedCommand parsedCommand) throws CommandNotFoundException;
	Collection<Command> getAvailableCommands();
	void register(String commandName,Command command);
}
