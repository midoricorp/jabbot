package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.BindingMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class MucCommandListener implements PacketListener{
	final Logger logger = LoggerFactory.getLogger(MucCommandListener.class);
	private List<BindingListener> listeners = new ArrayList<>();
	final XmppConnection binding;

	public MucCommandListener(XmppConnection binding, List<BindingListener> listeners) {
		this.binding = binding;
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);
	}

	@Override
	public void processPacket(Packet packet) throws SmackException.NotConnectedException {
		if(packet instanceof Message){
			Message message = (Message)packet;

			String from = StringUtils.parseResource(message.getFrom());
			String roomName = StringUtils.parseBareAddress(message.getFrom());
			BindingMessage m = new BindingMessage(roomName,from,message.getBody());
			logger.debug("received packet from {} with body: {}",message.getFrom(),message.getBody());

			for (BindingListener listener : listeners) {
				listener.onMessage(binding,m);
			}
		}
	}
}
