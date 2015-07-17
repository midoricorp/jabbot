package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.messaging.Message;

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
	 * @param message the  message to send to the room
	 * @return true if message has been sent properly
     * @see {@link org.wanna.jabbot.command.messaging.Message}
	 */
	boolean sendMessage(final Message message);

	/**
	 * Join on room on the current binding
	 * @param configuration the room configuration
	 * @return true if room as been joined properly
	 */
	boolean join(final RoomConfiguration configuration);

    /**
     * Retrieves the name of the room
     * @return room name
     */
	String getRoomName();
}
