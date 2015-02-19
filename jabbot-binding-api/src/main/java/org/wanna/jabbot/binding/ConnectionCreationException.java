package org.wanna.jabbot.binding;

/**
 * This exception is used to notify that an error occured while trying to create a {@link org.wanna.jabbot.binding.JabbotConnection}
 *
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-15
 */
public class ConnectionCreationException extends Exception{
	public ConnectionCreationException(String message) {
		super(message);
	}

	public ConnectionCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
