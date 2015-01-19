package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final Logger logger = LoggerFactory.getLogger(JabbotConnectionFactory.class);

	private Map<String,Class<? extends JabbotConnection>> registry = new HashMap<>();

	public JabbotConnection create(JabbotConnectionConfiguration connectionConfiguration){
		JabbotConnection connection = null;
		if(registry.containsKey(connectionConfiguration.getType())){
			Class<? extends JabbotConnection> clazz = registry.get(connectionConfiguration.getType());
			try {
				connection = clazz.getDeclaredConstructor(JabbotConnectionConfiguration.class).newInstance(connectionConfiguration);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.error("unable to instantiate binding for "+connectionConfiguration.getType());
			}
		}else{
			logger.warn("could not create {} binding",connectionConfiguration.getType());
		}
		return connection;
	}

	@Override
	public void register(String bindingName, Class<? extends JabbotConnection> clazz) {
		registry.put(bindingName,clazz);
	}
}
