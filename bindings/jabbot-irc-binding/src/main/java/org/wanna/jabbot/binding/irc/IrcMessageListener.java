package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.domain.messages.ChannelActionMsg;
import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.domain.messages.UserActionMsg;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.DefaultRxMessage;
import org.wanna.jabbot.binding.messaging.DefaultResource;
import org.wanna.jabbot.binding.messaging.RxMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-15
 */
public class IrcMessageListener extends VariousMessageListenerAdapter{
	final Logger logger = LoggerFactory.getLogger(IrcMessageListener.class);
	private final List<BindingListener> listeners;
	private final IrcBinding binding;

	public IrcMessageListener(IrcBinding binding,List<BindingListener> listeners) {
			this.binding = binding;
			this.listeners =(listeners == null ? new ArrayList<BindingListener>() : listeners);
	}

	@Override
	public void onChannelMessage(ChannelPrivMsg aMsg) {
		logger.debug("received {} on {}",aMsg.getText(),aMsg.getChannelName());
		String sender = aMsg.getSource().getNick();
		String roomName = aMsg.getChannelName();
		RxMessage request = new DefaultRxMessage(new DefaultMessageContent(aMsg.getText()),new DefaultResource(roomName,sender));
        for (BindingListener listener : listeners) {
			listener.eventReceived(new MessageEvent(binding,request));
		}
		super.onChannelMessage(aMsg);
	}
}
