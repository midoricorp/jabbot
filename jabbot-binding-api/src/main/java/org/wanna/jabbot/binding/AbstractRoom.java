package org.wanna.jabbot.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-19
 */
public abstract class AbstractRoom<T> implements Room<T>{
	protected T connection;

	protected AbstractRoom(T connection) {
		this.connection = connection;
	}

	/**
	 * {@inheritDoc}
	 */
	public T getConnection() {
		return connection;
	}

}
