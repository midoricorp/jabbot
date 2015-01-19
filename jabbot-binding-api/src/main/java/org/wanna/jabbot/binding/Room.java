package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.MucHolder;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public interface Room<C> extends MucHolder{
	RoomConfiguration getConfiguration();
	boolean sendMessage(String message);
	C getConnection();
	boolean join(final RoomConfiguration configuration);
}
