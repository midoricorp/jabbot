package org.wanna.jabbot.binding;

/**
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
