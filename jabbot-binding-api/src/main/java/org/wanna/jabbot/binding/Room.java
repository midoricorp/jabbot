package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.RoomConfiguration;

/**
 * This is the actual representation of a binding chatroom
 * Chatroom are used as a message recipient on which listener can be hooked
 * in order to create Command and provide a mechanism to send a response based on the command output.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public interface Room{

	/**
	 * Send a message to the room itself
	 * @param message the string message to send to the room
	 * @return true if message has been sent properly
	 */
	boolean sendMessage(String message);

	/**
	 * Join on room on the current binding
	 * @param configuration the room configuration
	 * @return true if room as been joined properly
	 */
	boolean join(final RoomConfiguration configuration);

	String getRoomName();
}
