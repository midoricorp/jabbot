package org.wanna.jabbot.config;

import org.wanna.jabbot.binding.config.BindingConfiguration;

/**
 * Interface to interact with the configuration storage.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public interface ConfigurationDao {
	/**
	 * Retrieves the whole JabbotConfiguration
	 * @return JabbotConfiguration object
	 */
	JabbotConfiguration getConfiguration();

	/**
	 * Saves the bot configuration
	 * @param configuration the configuration to save
	 * @return saved configuration
	 */
	JabbotConfiguration saveConfiguration(JabbotConfiguration configuration);
	/**
	 * Retrieves the list of Bindings for which Jabbot has been configured.
	 * @return Bindings array
	 */
	BindingConfiguration[] getBindings();

	/**
	 * Retrieves the Binding matching a given identifier
	 * @param identifier identifier of the binding to retrieve
	 * @return binding
	 */
	BindingConfiguration getBinding(String identifier);

	/**
	 * Add a new Binding to Jabbot
	 * @param configuration the binding to add
	 * @return added binding
	 */
	BindingConfiguration addBinding(BindingConfiguration configuration);
}
