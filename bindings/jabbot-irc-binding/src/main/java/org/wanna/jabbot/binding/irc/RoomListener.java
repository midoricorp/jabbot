package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.BindingMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-15
 */
public class RoomListener extends VariousMessageListenerAdapter{
	final Logger logger = LoggerFactory.getLogger(RoomListener.class);
	private final List<BindingListener> listeners;
	private final IrcBinding binding;

	public RoomListener(IrcBinding binding, List<BindingListener> listeners) {
		this.binding = binding;
		this.listeners =(listeners == null ? new ArrayList<BindingListener>() : listeners);
	}

	@Override
	public void onChannelMessage(ChannelPrivMsg aMsg) {

		logger.debug("received {} on {}",aMsg.getText(),aMsg.getChannelName());
		String sender = aMsg.getSource().getNick();
		String roomName = aMsg.getChannelName();
		BindingMessage message = new BindingMessage(roomName,sender,aMsg.getText());

		for (BindingListener listener : listeners) {
			listener.onMessage(binding,message);
		}
		super.onChannelMessage(aMsg);
	}
}
