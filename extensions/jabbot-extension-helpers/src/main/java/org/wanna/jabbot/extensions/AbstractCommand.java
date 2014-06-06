package org.wanna.jabbot.extensions;

import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.ParsedCommand;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public abstract class AbstractCommand implements Command {
	private ParsedCommand parsedCommand;

	public ParsedCommand getParsedCommand() {
		return parsedCommand;
	}

	public void setParsedCommand(ParsedCommand parsedCommand) {
		this.parsedCommand = parsedCommand;
	}


}
