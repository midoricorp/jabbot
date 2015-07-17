package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.util.XmppStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class MucCommandListener implements StanzaListener{
	final Logger logger = LoggerFactory.getLogger(MucCommandListener.class);
	private List<BindingListener> listeners = new ArrayList<>();
	final XmppBinding binding;

	public MucCommandListener(XmppBinding binding, List<BindingListener> listeners) {
		this.binding = binding;
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);
	}

	@Override
	public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
		if(packet instanceof org.jivesoftware.smack.packet.Message){
			org.jivesoftware.smack.packet.Message message = (org.jivesoftware.smack.packet.Message)packet;
			String from = XmppStringUtils.parseResource(message.getFrom());
			String roomName = XmppStringUtils.parseBareJid(message.getFrom());
            Message m = new DefaultMessage(message.getBody(),from,roomName);
            logger.debug("received packet from {} with body: {}",message.getFrom(),message.getBody());
			XmppRoom room = (XmppRoom)binding.getRoom(roomName);
			//If message is sent by myself, ignore it.
			if(room == null || from.equals(room.getConfiguration().getNickname())){
				logger.debug("ignoring packet as it's sent by myself");
				return;
			}
			for (BindingListener listener : listeners) {
				listener.onMessage(binding,m);
			}
		}
	}
}
