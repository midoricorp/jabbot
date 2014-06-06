package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-03
 */
public class CommandNotFoundException extends Exception{
	private final String commandName;

	public CommandNotFoundException(String commandName) {
		this.commandName = commandName;
	}

	public String getCommandName() {
		return commandName;
	}
}
