package org.wanna.jabbot.binding.matrix;




import de.jojii.matrixclientserver.Bot.Client;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;

import java.io.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.messaging.*;
import org.wanna.jabbot.messaging.body.BodyPart;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class MatrixBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(MatrixBinding.class);
	private boolean connected;
	private Client client;
	private String password;
	private String url;
	private String myUserId;

	public MatrixBinding(BindingConfiguration configuration) {
		super(configuration);
		this.password = configuration.getPassword();
		this.url = configuration.getUrl();
		logger.info("Configuring Matrix Binding with (" + configuration.getPassword() +")");
		client = new Client(this.url);
	}


	public void roomListener(List<RoomEvent> roomEvents) {
		for (RoomEvent event : roomEvents) {

			if (event.getType().equals("m.room.member") && event.getContent().has("membership") && event.getContent().getString("membership").equals("invite")) {
				try {
					//make bot autojoin
					client.joinRoom(event.getRoom_id(), null);
				} catch (IOException e) {
					logger.error("Unable to join matrix room", e);
				}
			} else if (event.getType().equals("m.room.message")) {
				//Sends a readreceipt  for every received message
				try {
					client.sendReadReceipt(event.getRoom_id(), event.getEvent_id(), "m.read", null);
				} catch (IOException e) {
					logger.error("Unable to ack message", e);
				}
				if (event.getSender().equals(myUserId)) {
					logger.info("Matrix: Ignoring message from myself!");
					return;
				}
				if (event.getContent().has("body")) {
					String msg = RoomEvent.getBodyFromMessageEvent(event);
					if (msg != null && msg.trim().length() > 0) {
						logger.info("Matrix got a message: " + msg);
						RxMessage request = new DefaultRxMessage(new DefaultMessageContent(msg), new DefaultResource(event.getRoom_id(), event.getSender()));
						MessageEvent messageEvent = new MessageEvent(this, request);
						dispatchEvent(messageEvent);
					}
				}
			}
		}
	}

	@Override
	public boolean connect() {
		logger.info("Logging into Matrix server");

		try {
			synchronized(client) {
				client.login(this.password, data -> {
					if (data.isSuccess()) {
						connected = true;
						myUserId = data.getUser_id();
						client.registerRoomEventListener(roomEvents->{roomListener(roomEvents);});
					} else {
						connected = false;
					}
					synchronized (client) {
						client.notify();
					}
				});
				client.wait();
			}
		} catch (IOException e) {
			logger.error("Unable to connect to matrix", e);
		} catch (InterruptedException e) {
			logger.error("Unable to connect to matrix", e);
		}
		logger.info("Login Completed");

		return connected;
	}

	@Override
	public boolean disconnect() {

		if(connected) {
			logger.info("Disconnecting from Matrix server");
			try {
				client.logout(() ->{} );
			} catch (IOException e) {
				logger.error("Unable to logout", e);
			}
			connected = false;
		}
		return connected;
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
		Resource resource = response.getRequest().getSender();

		String message = messageContent.getBody(BodyPart.Type.TEXT).getText();
		String formattedMessage = null;
		if (messageContent.getBody(BodyPart.Type.XHTML) != null) {
			formattedMessage = messageContent.getBody(BodyPart.Type.XHTML).getText();
			formattedMessage = new HtmlReformat(client, formattedMessage).invoke();
		}
		logger.info("Sending message: " + message + "to room " + resource.getAddress());
		try {
			client.sendText(resource.getAddress(),message, formattedMessage != null, formattedMessage, null);
		} catch (IOException e) {
			logger.error("unable to send matrix message", e);
		}

	}


}
