package org.wanna.jabbot;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingCreationException;
import org.wanna.jabbot.binding.BindingFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class JabbotBindingFactory implements BindingFactory {

	private Map<String,Class<? extends Binding>> registry = new HashMap<>();

	public Binding create(BindingConfiguration connectionConfiguration) throws BindingCreationException {
		Binding connection;
		if(!registry.containsKey(connectionConfiguration.getType())){
			throw new BindingCreationException("No binding found for type "+connectionConfiguration.getType());
		}

		Class<? extends Binding> clazz = registry.get(connectionConfiguration.getType());
		try {
			connection = clazz.getDeclaredConstructor(BindingConfiguration.class).newInstance(connectionConfiguration);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new BindingCreationException("binding instantiation error",e);
		}
		return connection;
	}

	@Override
	public void register(String bindingName, Class<? extends Binding> clazz) {
		registry.put(bindingName,clazz);
	}
}
