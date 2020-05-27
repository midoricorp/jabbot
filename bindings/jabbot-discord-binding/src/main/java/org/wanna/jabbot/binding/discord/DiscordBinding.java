package org.wanna.jabbot.binding.discord;



import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.ConnectionException;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.event.DisconnectedEvent;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.messaging.*;
import org.wanna.jabbot.messaging.body.BodyPart;



import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class DiscordBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(DiscordBinding.class);
	private boolean connected;
	private DiscordApi client;
	private String password;

	public DiscordBinding(BindingConfiguration configuration) {
		super(configuration);
		this.password = configuration.getPassword();
		logger.info("Configuring Discord Binding with (" + configuration.getPassword() +")");
	}

	class DiscordResource implements Resource {

		TextChannel channel;
		String user;

		public DiscordResource(TextChannel channel, String user) {
			this.channel = channel;
			this.user = user;
		}

		@Override
		public String getAddress() {
			return channel.toString();
		}

		@Override
		public String getName() {
			return user;
		}

		@Override
		public Type getType() {
			return Type.ROOM;
		}
	}


	@Override
	public boolean connect() {
		logger.info("Logging into Discord server");
		client = new DiscordApiBuilder().setToken(password).login().join();
		client.addMessageCreateListener(event -> {
			onMessage(event);
		});
		connected = true;

		logger.info("Login Completed");

		return connected;
	}

	@Override
	public boolean disconnect() {

		if(connected) {
			logger.info("Disconnecting from DISCORD server");
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
		DiscordResource resource = (DiscordResource)response.getRequest().getSender();

		String message = messageContent.getBody(BodyPart.Type.TEXT).getText();
		logger.info("Sending message: " + message);
		message = message.replaceAll("([*_~])", "\\\\$1");
		resource.channel.sendMessage(message);
	}

	private void onMessage(MessageCreateEvent event) {
		TextChannel channel = event.getChannel();
		if(event.getMessage().getAuthor().isBotUser()) {
			return;
		}
		String username = event.getMessage().getAuthor().getDisplayName();
		String message = event.getMessage().getContent();

		if(message.length() == 0) {
			message = "^_^";
		}
		RxMessage request = new DefaultRxMessage(new DefaultMessageContent(message), new DiscordResource(channel, username));
		MessageEvent messageEvent = new MessageEvent(this, request);
		dispatchEvent(messageEvent);
	}

}
