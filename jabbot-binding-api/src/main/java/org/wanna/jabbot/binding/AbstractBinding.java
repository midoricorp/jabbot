package org.wanna.jabbot.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for a {@link Binding} implementation.
 * It instantiate and populate the Binding {@link BindingFactory}
 * based on the binding configuration
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public abstract class AbstractBinding<T> implements Binding, CommandFactoryAware {
	private final Logger logger = LoggerFactory.getLogger(AbstractBinding.class);
	protected T connection;
	protected CommandFactory commandFactory;
	private BindingConfiguration configuration;
	protected List<BindingListener> listeners = new ArrayList<>();

	protected AbstractBinding(BindingConfiguration configuration) {
		this.configuration = configuration;
		logger.debug("initializing command factory for {}",configuration.getUrl());
	}

	/**
	 * {@inheritDoc}
	 */
	public T getWrappedConnection() {
		return connection;
	}

	/**
	 * Returns the underlying connection object.
	 * example smack XMPPConnection object
	 *
	 * @return T underlying connection
	 */
	public BindingConfiguration getConfiguration(){
		return configuration;
	}

	@Override
	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	@Override
	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	@Override
	public void registerListener(BindingListener listener) {
		listeners.add(listener);
	}

	@Override
	public void sendMessage(BindingMessage message) {
		Room room = this.getRoom(message.getRoomName());
		room.sendMessage(message.getBody());
	}
}
