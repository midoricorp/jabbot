package org.wanna.jabbot.binding;

/**
 * This exception is used to notify that an error occured while trying to create a {@link Binding}
 *
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-15
 */
public class BindingCreationException extends Exception{
	public BindingCreationException(String message) {
		super(message);
	}

	public BindingCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
