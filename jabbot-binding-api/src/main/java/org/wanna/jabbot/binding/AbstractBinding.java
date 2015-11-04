package org.wanna.jabbot.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.messaging.body.BodyPart;
import org.wanna.jabbot.binding.messaging.body.BodyPartValidator;
import org.wanna.jabbot.binding.messaging.body.BodyPartValidatorFactory;
import org.wanna.jabbot.binding.messaging.body.InvalidBodyPartException;

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
	private final Logger logger = LoggerFactory.getLogger(AbstractBinding.class);
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

	@Override
	public void sendMessage(BindingMessage message) {
		Room room = this.getRoom(message.getRoomName());
        room.sendMessage(message);
	}

    /**
     * Updates the initial message with the command result
     *
     * @param message the message to be updated
     * @param commandResult the command result
     * @return updated message
     */
    @SuppressWarnings("unchecked")
    public Message createResponseMessage(final BindingMessage message, final Message commandResult){
        DefaultBindingMessage response = new DefaultBindingMessage();
        response.setDestination(message.getSender());
        response.setSender(message.getDestination());
        response.setRoomName(message.getRoomName());
        for (BodyPart body : commandResult.getBodies()) {
            BodyPartValidator validator = BodyPartValidatorFactory.getInstance().create(body.getType());
            try {
                //If a validator exists for that body type, validate the message
                if(validator != null){
                    validator.validate(body);
                }
                response.addBody(body);
            } catch (InvalidBodyPartException e) {
                logger.info("discarding XhtmlBodyPart as it's content is declared invalid: {}"
                        ,(e.getInvalidBodyPart()==null?"NULL":e.getInvalidBodyPart().getText()));
            }
        }
        return response;
    }
}
