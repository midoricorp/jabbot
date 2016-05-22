package org.wanna.jabbot.binding.xmpp;

import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.MessageContent;
import org.wanna.jabbot.binding.messaging.Resource;
import org.wanna.jabbot.binding.messaging.RxMessage;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-14
 */
public class XmppRxMessage implements RxMessage {
    private String id;
    private String thread;
    private Resource sender;
    private String roomName;
    private MessageContent content = new DefaultMessageContent();

    public XmppRxMessage(Resource sender, MessageContent content) {
        this.sender = sender;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Resource getSender() {
        return sender;
    }

    public void setSender(Resource sender) {
        this.sender = sender;
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

    @Override
    public MessageContent getMessageContent() {
        return content;
    }
}
