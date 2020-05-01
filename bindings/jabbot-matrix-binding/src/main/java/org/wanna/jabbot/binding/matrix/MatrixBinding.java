package org.wanna.jabbot.binding.matrix;




import de.jojii.matrixclientserver.Bot.Client;
import de.jojii.matrixclientserver.Bot.Events.RoomEvent;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
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

	public MatrixBinding(BindingConfiguration configuration) {
		super(configuration);
		this.password = configuration.getPassword();
		logger.info("Configuring Discord Binding with (" + configuration.getPassword() +")");
	}



	@Override
	public boolean connect() {
		logger.info("Logging into Matrix server");

		connected = true;

		logger.info("Login Completed");

		return connected;
	}

	@Override
	public boolean disconnect() {

		if(connected) {
			logger.info("Disconnecting from Matrix server");
			client.disconnect();;
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

		String message = messageContent.getBody(BodyPart.Type.TEXT).getText();
		logger.info("Sending message: " + message);

	}



}
