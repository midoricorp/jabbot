package org.wanna.jabbot.extensions;

import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.parser.ParsedCommand;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public abstract class AbstractCommand implements Command {
	private ParsedCommand parsedCommand;
	private String commandName;

	protected AbstractCommand(String commandName) {
		this.commandName = commandName;
	}

	protected AbstractCommand() {
	}

	public ParsedCommand getParsedCommand() {
		return parsedCommand;
	}

	public void setParsedCommand(ParsedCommand parsedCommand) {
		this.parsedCommand = parsedCommand;
	}

	public String getCommandName() {
		return commandName;
	}
}
