package org.wanna.jabbot.command;

import org.wanna.jabbot.command.parser.args.ArgsParser;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public interface Command {
	/**
	 * Return the argument parser implementation that will be used to pre-process the args line
	 * Prior to get them passed the MessageWrapper & invoke process method
	 * @return ArgsParser implementation
	 */
	ArgsParser getArgsParser();
	/**
	 * Returns the name under which the command is registered
	 *
	 * @return String command name
	 */
	String getCommandName();

	/**
	 * Returns a short description of the command
	 * @return String command description
	 */
	String getDescription();

	/**
	 * Returns the command help
	 * @return String help message
	 */
	String getHelpMessage();

	/**
	 * Execute the command for a given message in a chat room
	 *
	 * @param message message which triggered the command
	 */
	CommandResult process(MessageWrapper message);
}

