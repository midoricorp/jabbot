package org.wanna.jabbot.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.messaging.Resource;
import org.wanna.jabbot.messaging.TxMessage;

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
public abstract class AbstractBinding<T> implements Binding<T>{
	private static final Logger logger = LoggerFactory.getLogger(AbstractBinding.class);
	protected T connection;
	private BindingConfiguration configuration;
	protected List<BindingListener> listeners = new ArrayList<>();

	protected AbstractBinding(BindingConfiguration configuration) {
		this.configuration = configuration;
		logger.debug("initializing command factory for {}",configuration.getUrl());
	}

	/**
	 * {@inheritDoc}
	 */
	public T getConnection() {
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
	public void registerListener(BindingListener listener) {
		listeners.add(listener);
	}

	public void sendMessage(TxMessage response){
		if(response.getDestination().getType().equals(Resource.Type.ROOM)){
			Room room = this.getRoom(response.getDestination().getAddress());
			if(room != null){
				room.sendMessage(response);
			}
		}
	}

	public void dispatchEvent(BindingEvent event){
		for (BindingListener listener : listeners) {
			listener.eventReceived(event);
		}
	}

	public String toString(){
		return String.format("%s{id: %s}", this.getClass().getSimpleName(),configuration.getId());
	}

	@Override
	public String getIdentifier() {
		return configuration.getId();
	}
}
