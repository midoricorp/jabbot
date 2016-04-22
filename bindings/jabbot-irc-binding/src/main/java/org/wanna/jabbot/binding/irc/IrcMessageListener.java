package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.domain.messages.ErrorMessage;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.DefaultBindingMessage;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-15
 */
public class IrcMessageListener extends VariousMessageListenerAdapter{
	final Logger logger = LoggerFactory.getLogger(IrcMessageListener.class);
	private final List<BindingListener> listeners;

	public IrcMessageListener(List<BindingListener> listeners) {
		this.listeners =(listeners == null ? new ArrayList<BindingListener>() : listeners);
	}

	@Override
	public void onChannelMessage(ChannelPrivMsg aMsg) {

		logger.debug("received {} on {}",aMsg.getText(),aMsg.getChannelName());
		String sender = aMsg.getSource().getNick();
		String roomName = aMsg.getChannelName();
        DefaultBindingMessage message = new DefaultBindingMessage();
        message.addBody(new TextBodyPart(aMsg.getText()));
        message.setSender(new IrcResource(sender,null));
        message.setDestination(new IrcResource(roomName,null));
        message.setRoomName(roomName);
        for (BindingListener listener : listeners) {
			listener.onMessage(message);
		}
		super.onChannelMessage(aMsg);
	}
}
