package org.wanna.jabbot;

import org.wanna.jabbot.binding.ConnectionCreationException;
import org.wanna.jabbot.binding.ConnectionFactory;
import org.wanna.jabbot.binding.JabbotConnection;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class JabbotConnectionFactory implements ConnectionFactory {

	private Map<String,Class<? extends JabbotConnection>> registry = new HashMap<>();

	public JabbotConnection create(JabbotConnectionConfiguration connectionConfiguration) throws ConnectionCreationException{
		JabbotConnection connection;
		if(!registry.containsKey(connectionConfiguration.getType())){
			throw new ConnectionCreationException("No binding found for type "+connectionConfiguration.getType());
		}

		Class<? extends JabbotConnection> clazz = registry.get(connectionConfiguration.getType());
		try {
			connection = clazz.getDeclaredConstructor(JabbotConnectionConfiguration.class).newInstance(connectionConfiguration);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ConnectionCreationException("binding instantiation error",e);
		}
		return connection;
	}

	@Override
	public void register(String bindingName, Class<? extends JabbotConnection> clazz) {
		registry.put(bindingName,clazz);
	}
}
