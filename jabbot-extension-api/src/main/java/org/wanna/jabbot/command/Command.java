package org.wanna.jabbot.command;

import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.parser.args.ArgsParser;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public interface Command {
	/**
	 * Return the argument parser implementation
	 * @return
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
	 * Returns the Command configuration object which has been used to 
	 * Configure the command during initialization
	 *
	 * @see org.wanna.jabbot.command.config.CommandConfig
	 * @return Command configuration
	 */
	CommandConfig getConfiguration();

	/**
	 * Execute the command for a given message in a chat room
	 *
	 * @param message message which triggered the command
	 */
	CommandResult process(MessageWrapper message);
}

