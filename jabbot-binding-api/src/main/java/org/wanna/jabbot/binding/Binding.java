package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.messaging.Message;

/**
 * A Binding represent an implementation of a Chat protocol such like XMPP, IRC, etc.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public interface Binding<T> {
	/**
	 * Create a connection to the messaging service
	 * @return true if connection is properly established.
	 */
	boolean connect();

	/**
	 * Check if a connection to an instant messaging service is properly established.
	 *
	 * @return true if connection is established.
	 */
	boolean isConnected();

	/**
	 * Join a chatroom for the current connection
	 *
	 * @param configuration the chatromm configuration
	 * @return the joined chatroom
	 * @see {@link org.wanna.jabbot.binding.config.RoomConfiguration}
	 */
	Room joinRoom(RoomConfiguration configuration);

	Room getRoom(String roomName);

	void registerListener(BindingListener listener);

	void sendMessage(BindingMessage message);

    Message createResponseMessage(BindingMessage source, Message eventResponse);

    /**
     * Retrieves the connection
     * @return connection
     */
    T getConnection();

	BindingConfiguration getConfiguration();
}
