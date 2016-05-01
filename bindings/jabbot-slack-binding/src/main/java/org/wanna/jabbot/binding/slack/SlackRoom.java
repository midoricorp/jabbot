package org.wanna.jabbot.binding.slack;

import com.sipstacks.xhml.XHtmlConvertException;
import flowctrl.integration.slack.type.Attachment;
import flowctrl.integration.slack.type.Channel;
import flowctrl.integration.slack.type.User;
import flowctrl.integration.slack.webapi.method.chats.ChatPostMessageMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.DefaultBindingMessage;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.messaging.DefaultResource;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.messaging.body.BodyPart;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.sipstacks.xhml.Slack;
import com.sipstacks.xhml.XHTMLObject;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class SlackRoom extends AbstractRoom<SlackBinding>  {
	private final static Logger logger = LoggerFactory.getLogger(SlackRoom.class);
	private final List<BindingListener> listeners;
	private RoomConfiguration configuration;
	public String channelId;


	public SlackRoom(SlackBinding connection, List<BindingListener> listeners) {
		super(connection);
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);

	}


	public void dispatchMessage(flowctrl.integration.slack.type.Message slackMsg) {
		if(slackMsg.getUser() == null) {
			logger.error("Message missing user!" + slackMsg.toString());
			return;
		}
		User user = connection.webApiClient.getUserInfo(slackMsg.getUser());
		String slackMsgText = slackMsg.getText();
		slackMsgText = slackMsgText.replaceAll("<(http[^\">]*)>", "$1");
		slackMsgText = slackMsgText.replace("&gt;", ">");
		slackMsgText = slackMsgText.replace("&lt;", "<");
		slackMsgText = slackMsgText.replace("&amp;", "&");


		for (BindingListener listener : listeners) {
			DefaultBindingMessage message = new DefaultBindingMessage();

			message.addBody(new TextBodyPart(slackMsgText));
			message.setSender(new DefaultResource(this.getRoomName(), user.getName()));
			message.setDestination(new DefaultResource(this.getRoomName(), null));
			message.setRoomName(this.getRoomName());
			listener.eventReceived(new MessageEvent(this.connection, message));
		}

	}

	@Override
	public boolean sendMessage(Message message) {
		ChatPostMessageMethod cpmm = new ChatPostMessageMethod(channelId, "Formatted Response:");
		if(message.getBody(BodyPart.Type.XHTML) != null) {
			XHTMLObject obj = new XHTMLObject();

			try {
				logger.info("About to convert:" + message.getBody(BodyPart.Type.XHTML).getText());
				obj.parse(message.getBody(BodyPart.Type.XHTML).getText());
				List<Attachment> attachments = Slack.convert(obj);
				attachments.get(0).setFallback(message.getBody());
				cpmm.setAttachments(attachments);
			} catch (XHtmlConvertException e) {
				logger.error("Unable to parse xhtml!", e);
			}

		} else {
			if(message.getBody().length() == 0) {
				cpmm.setText("^_^");
			} else {
				String body = message.getBody();
				body = body.replaceAll("&", "&amp;");
				body = body.replaceAll("<", "&lt;");
				body = body.replaceAll(">", "&gt;");;
				cpmm.setText(body);
			}
		}
		cpmm.setUsername(configuration.getNickname());
		cpmm.setAs_user(false);

		logger.info("Sending message:" + cpmm.toString());
		connection.webApiClient.postMessage(cpmm);
		//connection.webApiClient.postMessage(channelId, message.getBody(), configuration.getNickname(), false);
		return true;
	}

	@Override
	public boolean join(final RoomConfiguration configuration) {
		this.configuration = configuration;

		List<Channel> channels = connection.webApiClient.getChannelList();
		for (Channel channel : channels) {
			logger.info("Comparing " + channel.getName() + " to " + configuration.getName());
			if(channel.getName().equals(configuration.getName())) {
				channelId  = channel.getId();
			}
		}

		if (channelId == null) {
			logger.error("Room "+ configuration.getName() + " not found!");
		}
		return channelId != null;
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}
}
