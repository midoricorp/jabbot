package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.BindingConfiguration;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-17
 */
public interface ConnectionFactory {
	/**
	 * Instantiate a binding based on the connection configuration which is passed to it.
	 * The returned connection won't initiate the connection to the defined server.
	 * and thus a call to {@link Binding#connect(org.wanna.jabbot.binding.config.BindingConfiguration)} will be required in order to initiate the connection.
	 *
	 * @param connectionConfiguration the binding configuration
	 * @return a preconfigured connection
	 * @throws BindingCreationException if no binding is found with such configuration or if any instantiation exception is thrown.
	 */
	Binding create(BindingConfiguration connectionConfiguration) throws BindingCreationException;

	/**
	 * Link a {@link Binding} class to a binding name
	 *
	 * @param bindingName the unique identifier assigned to the binding
	 * @param clazz the binding class
	 */
	void register(String bindingName,Class<? extends Binding> clazz);
}
