package org.wanna.jabbot.binding.slack;

import com.sipstacks.xhml.Slack;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;
/*import allbegray.slack.SlackClientFactory;
import allbegray.slack.rtm.Event;
import allbegray.slack.rtm.EventListener;
import allbegray.slack.rtm.SlackRealTimeMessagingClient;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.User;
import allbegray.slack.webapi.SlackWebApiClient;
import allbegray.slack.webapi.method.chats.ChatPostMessageMethod;*/
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.users.UsersInfoResponse;
import com.slack.api.rtm.*;
import emoji4j.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.ConnectionException;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.messaging.*;
import org.wanna.jabbot.messaging.body.BodyPart;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class SlackBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(SlackBinding.class);
	private boolean connected;
	private com.slack.api.Slack slack;
	private RTMClient rtmClient;
	RTMEventsDispatcher dispatcher;
	private MethodsClient webApiClient;
	static {
		EmojiManager.addStopWords(":[0-9]+");
	}

	public SlackBinding(BindingConfiguration configuration) {
		super(configuration);
	}

	@Override
	public boolean connect() {
		logger.info("Creating RTM Client");
		try {
			slack = com.slack.api.Slack.getInstance();
			rtmClient = slack.rtm(getConfiguration().getPassword());
			dispatcher = RTMEventsDispatcherFactory.getInstance();
			webApiClient = slack.methods(getConfiguration().getPassword());
		} catch (Throwable e) {
			throw new ConnectionException(e);
		}
		logger.info("RTM Client created! connecting");
		RTMEventHandler<com.slack.api.model.event.MessageEvent> messageHandler = new RTMEventHandler<com.slack.api.model.event.MessageEvent>() {
			@Override
			public void handle(com.slack.api.model.event.MessageEvent messageEvent) {
				logger.info("Got a message: " + messageEvent.getText());
				dispatchMessage(messageEvent);			}
		};
		dispatcher.register(messageHandler);

		RTMCloseHandler closeHandler = new RTMCloseHandler() {
			@Override
			public void handle(CloseReason reason) {
				logger.info("RTP Closed, reason: " + reason.getReasonPhrase());
				connected = false;
			}
		};

		try {
			rtmClient.connect();
			rtmClient.addMessageHandler(dispatcher.toMessageHandler());
			rtmClient.addCloseHandler(closeHandler);
			logger.info("RTP Connected");
			connected = true;
			super.dispatchEvent(new ConnectedEvent(this));
		}catch(Throwable e){
			throw new ConnectionException(e);
		}
		return connected;
	}

	@Override
	public boolean disconnect() {
		try {
			rtmClient.close();
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
		connected = false;
		//webApiClient.shutdown();
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		return null;
	}

	@Override
	public boolean isConnected() {

		return connected;
	}

	@Override
	public Room getRoom(String roomName) {
		return null;
	}

	@Override
	public void sendMessage(TxMessage response) {
		MessageContent messageContent = response.getMessageContent();
		try {
			webApiClient.chatPostMessage(req -> { ChatPostMessageRequest.ChatPostMessageRequestBuilder newReq = req;
				newReq.channel(response.getDestination().getAddress());
				newReq.username(this.getConfiguration().getUsername());

				/*
				if(messageContent.getBody(BodyPart.Type.XHTML) != null) {
					XHTMLObject obj = new XHTMLObject();

					try {
						logger.info("About to convert:" + messageContent.getBody(BodyPart.Type.XHTML).getText());
						obj.parse(messageContent.getBody(BodyPart.Type.XHTML).getText());
						List<Attachment> attachments = Slack.convert(obj);
						attachments.get(0).setFallback(messageContent.getBody());
						cpmm.setAttachments(attachments);
					} catch (XHtmlConvertException e) {
						logger.error("Unable to parse xhtml!", e);
					}

				} else {*/
					if(messageContent.getBody().length() == 0) {
						newReq.text("^_^");
					} else {
						String body = messageContent.getBody();
						body = body.replaceAll("&", "&amp;");
						body = body.replaceAll("<", "&lt;");
						body = body.replaceAll(">", "&gt;");

						// repair special link types
						body = body.replaceAll("&lt;@([|0-9A-Za-z]+)&gt;", "<@$1>");
						body = body.replaceAll("&lt;#([|0-9A-Za-z]+)&gt;", "<#$1>");

						logger.info("Sending messageContent: " + body);

						newReq.text(body);
					}
				//}
				return newReq;

			});
		} catch (IOException | SlackApiException e) {
			logger.error("Unable to send message", e);
		}

	}

	private void dispatchMessage(com.slack.api.model.event.MessageEvent slackMsg) {
		String username = slackMsg.getUser();

		if(username == null) {
			logger.error("MessageContent missing user!" + slackMsg.toString());
			return;
		}

		/* TO FIX */
		UsersInfoResponse usersInfoResponse = null;
		try {
			usersInfoResponse = webApiClient.usersInfo(req -> {
				req.user(slackMsg.getUser());
				return req;
			});
		} catch (IOException | SlackApiException e) {
			logger.error("Error finding user " + username, e);
		}
		username = usersInfoResponse.getUser().getName();

		String slackMsgText = slackMsg.getText();
		slackMsgText = slackMsgText.replaceAll("<(http[^\"|>]*)[|]([^|\">]*)>", "$2");
		slackMsgText = slackMsgText.replaceAll("<(http[^\">]*)>", "$1");
		slackMsgText = slackMsgText.replace("&gt;", ">");
		slackMsgText = slackMsgText.replace("&lt;", "<");
		slackMsgText = slackMsgText.replace("&amp;", "&");

		RxMessage request = new DefaultRxMessage(new DefaultMessageContent(slackMsgText), new DefaultResource(slackMsg.getChannel(),username));
		MessageEvent messageEvent = new MessageEvent(this, request);
		dispatchEvent(messageEvent);


	}

}
