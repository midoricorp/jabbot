package org.wanna.jabbot.command.behavior;

import org.wanna.jabbot.command.CommandFactory;

/**
 * Interface which indicates that the implementer is CommandFactory aware.
 * Any commands implementing this interface will have the CommandFactory of its binding injected
 * This will allow the command to spawn other commands or browse the list of the available commands
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-03
 */
public interface CommandFactoryAware {
	/**
	 * Setter for the CommandFactory
	 * @param commandFactory CommandFactory of the current binding
	 */
	void setCommandFactory(CommandFactory commandFactory);

	/**
	 * Invoked after all commands have been loaded in the command factory
	 */
	void onCommandsLoaded();
}
