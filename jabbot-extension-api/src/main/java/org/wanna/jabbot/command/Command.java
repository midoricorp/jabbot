package org.wanna.jabbot.command;

import org.wanna.jabbot.command.parser.ParsedCommand;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public interface Command {
	String getCommandName();
	ParsedCommand getParsedCommand();
	void setParsedCommand(ParsedCommand parsedCommand);
	void process(MucHolder chatroom, MessageWrapper message);
}

