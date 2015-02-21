package org.wanna.jabbot.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for a {@link org.wanna.jabbot.binding.JabbotConnection} implementation.
 * It instantiate and populate the Binding {@link org.wanna.jabbot.binding.ConnectionFactory}
 * based on the binding configuration
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public abstract class AbstractJabbotConnection<T> implements JabbotConnection<T>, CommandFactoryAware {
	private final Logger logger = LoggerFactory.getLogger(AbstractJabbotConnection.class);
	protected T connection;
	protected CommandFactory commandFactory;
	private JabbotConnectionConfiguration configuration;
	protected List<BindingListener> listeners = new ArrayList<>();

	protected AbstractJabbotConnection(JabbotConnectionConfiguration configuration) {
		this.configuration = configuration;
		logger.debug("initializing command factory for {}",configuration.getUrl());
	}

	/**
	 * {@inheritDoc}
	 */
	public T getWrappedConnection() {
		return connection;
	}

	public JabbotConnectionConfiguration getConfiguration(){
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
}
