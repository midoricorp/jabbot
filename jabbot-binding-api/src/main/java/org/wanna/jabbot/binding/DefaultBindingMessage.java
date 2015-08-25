package org.wanna.jabbot.binding;

import org.wanna.jabbot.command.messaging.body.BodyPart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-20
 */
public class DefaultBindingMessage implements BindingMessage{
    private String sender;
    private String destination;
    private String roomName;
    private Map<BodyPart.Type,BodyPart> bodies;

    public DefaultBindingMessage() {
        bodies = new HashMap<>();
    }

    @Override
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String getBody() {
        BodyPart bodyPart = getBody(BodyPart.Type.TEXT);
        if(bodyPart == null ){
            return null;
        }else{
            return bodyPart.getText();
        }
    }

    @Override
    public BodyPart getBody(BodyPart.Type type) {
        return bodies.get(type);
    }

    @Override
    public Collection<BodyPart> getBodies() {
        return bodies.values();
    }

    @Override
    public void addBody(BodyPart body) {
        bodies.put(body.getType(),body);
    }
}
