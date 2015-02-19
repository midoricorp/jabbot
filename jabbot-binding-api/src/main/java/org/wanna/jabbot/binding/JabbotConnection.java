package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.RoomConfiguration;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public interface JabbotConnection<T> {
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
	 * Returns the underlying connection object.
	 * example smack XMPPConnection object
	 *
	 * @return T underlying connection
	 */
	T getWrappedConnection();

	/**
	 * Join a chatroom for the current connection
	 *
	 * @param configuration the chatromm configuration
	 * @return the joined chatroom
	 * @see {@link org.wanna.jabbot.binding.config.RoomConfiguration}
	 */
	Room joinRoom(RoomConfiguration configuration);
}
