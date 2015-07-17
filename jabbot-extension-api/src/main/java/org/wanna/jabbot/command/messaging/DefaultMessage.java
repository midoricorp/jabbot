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
public class DefaultMessage implements Message {
	private String sender;
	private String roomName;
    private Map<BodyPart.Type,BodyPart> bodies;

    public DefaultMessage() {
        this(null);
    }

    public DefaultMessage(String text){
        this(text, null, null);
    }

    public DefaultMessage(String body, String sender, String roomName) {
        bodies = new HashMap<>();
        bodies.put(BodyPart.Type.TEXT,new TextBodyPart(body));
        this.sender = sender;
        this.roomName = roomName;
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
    public BodyPart getBody(String type){
        BodyPart.Type bodyType = BodyPart.Type.valueOf(type);
        if( bodyType == null ){
            bodyType = BodyPart.Type.TEXT;
        }
        return bodies.get(bodyType);
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
     * @deprecated usage {@link org.wanna.jabbot.command.messaging.DefaultMessage#addBody(org.wanna.jabbot.command.messaging.body.BodyPart)} instead
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

	@Override
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
