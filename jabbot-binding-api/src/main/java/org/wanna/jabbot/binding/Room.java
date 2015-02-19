package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.MucHolder;

/**
 * This is the actual representation of a binding chatroom
 * Chatroom are used as a message recipient on which listener can be hooked
 * in order to create Command and provide a mechanism to send a response based on the command output.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public interface Room<C> extends MucHolder{
	/**
	 * Retrieve the room configuration
	 * @return
	 */
	RoomConfiguration getConfiguration();

	/**
	 * Send a message to the room itself
	 * @param message
	 * @return
	 */
	boolean sendMessage(String message);

	/**
	 * Retrieve the @{@link org.wanna.jabbot.binding.JabbotConnection} to which this Room is bound.
	 * @return connection
	 */
	C getConnection();

	/**
	 * Join on room on the current binding
	 * @param configuration the room configuration
	 * @return
	 */
	boolean join(final RoomConfiguration configuration);
}
