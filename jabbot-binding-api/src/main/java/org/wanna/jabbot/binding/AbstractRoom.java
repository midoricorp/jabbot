package org.wanna.jabbot.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-19
 */
public abstract class AbstractRoom<T> implements Room{
	protected T connection;

	protected AbstractRoom(T connection) {
		this.connection = connection;
	}

	/**
	 * Retrieve the @{@link Binding} to which this Room is bound.
	 * @return connection
	 */
	public T getConnection() {
		return connection;
	}

}
