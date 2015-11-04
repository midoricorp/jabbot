package org.wanna.jabbot.binding.xmpp;

import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.binding.messaging.Resource;
import org.wanna.jabbot.binding.messaging.body.BodyPart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-14
 */
public class XmppMessage implements BindingMessage {
    private String id;
    private String thread;
    private Map<BodyPart.Type,BodyPart> bodies = new HashMap<>();
    private Resource sender;
    private Resource destination;
    private String roomName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getBody() {
        BodyPart part = getBody(BodyPart.Type.TEXT);
        if(part != null){
            return part.getText();
        }else{
            return null;
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

    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public Resource getSender() {
        return sender;
    }

    public void setSender(Resource sender) {
        this.sender = sender;
    }

    @Override
    public Resource getDestination() {
        return destination;
    }

    public void setDestination(Resource destination) {
        this.destination = destination;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
