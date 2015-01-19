package org.wanna.jabbot.command.behavior;

import org.wanna.jabbot.command.CommandFactory;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-03
 */
public interface CommandFactoryAware {
	void setCommandFactory(CommandFactory commandFactory);
}
