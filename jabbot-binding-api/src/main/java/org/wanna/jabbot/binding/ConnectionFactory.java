package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-17
 */
public interface ConnectionFactory {
	/**
	 * Instantiate a binding based on the connection configuration which is passed to it.
	 * The returned connection won't initiate the connection to the defined server.
	 * and thus a call to {@link JabbotConnection#connect(org.wanna.jabbot.binding.config.JabbotConnectionConfiguration)} will be required in order to initiate the connection.
	 *
	 * @param connectionConfiguration the binding configuration
	 * @return a preconfigured connection
	 * @throws ConnectionCreationException if no binding is found with such configuration or if any instantiation exception is thrown.
	 */
	JabbotConnection create(JabbotConnectionConfiguration connectionConfiguration) throws ConnectionCreationException;

	/**
	 * Link a {@link org.wanna.jabbot.binding.JabbotConnection} class to a binding name
	 *
	 * @param bindingName the unique identifier assigned to the binding
	 * @param clazz the binding class
	 */
	void register(String bindingName,Class<? extends JabbotConnection> clazz);
}
