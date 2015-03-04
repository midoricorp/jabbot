package org.wanna.jabbot.command.behavior;

import java.util.Map;

/**
 * Allow an object to receive extra arguments as a Map during initialization phase.
 * The Map will be fed from the json config file.
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-25
 */
public interface Configurable {
	/**
	 * Initialize the object with the given configuration
	 *
	 * @param configuration configuration to be applied.
	 */
	void configure(Map<String,Object> configuration);
}
