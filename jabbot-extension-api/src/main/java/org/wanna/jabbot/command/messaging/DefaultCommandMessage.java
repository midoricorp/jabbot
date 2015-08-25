package org.wanna.jabbot.command.messaging;

import org.wanna.jabbot.command.messaging.body.BodyPart;
import org.wanna.jabbot.command.messaging.body.TextBodyPart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-21
 */
public class DefaultCommandMessage implements CommandMessage {
	private String sender;
    private Map<BodyPart.Type,BodyPart> bodies;

    public DefaultCommandMessage() {
        this(null);
    }

    public DefaultCommandMessage(String text){
        this(text, null);
    }

    public DefaultCommandMessage(String body, String sender){
        bodies = new HashMap<>();
        bodies.put(BodyPart.Type.TEXT, new TextBodyPart(body));
        this.sender = sender;
    }

    /**
     * {@inheritDoc}
     */
    public String getBody() {
        BodyPart body = bodies.get(BodyPart.Type.TEXT);
        if(body == null){
            return null;
        }else{
            return body.getText();
        }
	}

    /**
     * {@inheritDoc}
     */
    public BodyPart getBody(BodyPart.Type type){
        return bodies.get(type);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<BodyPart> getBodies(){
        return bodies.values();
    }

    /**
     * {@inheritDoc}
     */
    public void addBody(BodyPart body){
        if(body != null){
            bodies.put(body.getType(),body);
        }
    }

    /**
     * Set the message body as a TextBodyPart
     *
     * @param body text to set as TextBodyPart
     * @deprecated usage {@link DefaultCommandMessage#addBody(org.wanna.jabbot.command.messaging.body.BodyPart)} instead
     * @see {@link org.wanna.jabbot.command.messaging.body.BodyPart}
     */
    public void setBody(String body) {
		bodies.put(BodyPart.Type.TEXT,new TextBodyPart(body));
	}

	@Override
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
