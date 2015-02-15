package org.wanna.jabbot.binding;

import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-17
 */
public interface ConnectionFactory {
	JabbotConnection create(JabbotConnectionConfiguration connectionConfiguration) throws ConnectionCreationException;
	void register(String bindingName,Class<? extends JabbotConnection> clazz);
}
