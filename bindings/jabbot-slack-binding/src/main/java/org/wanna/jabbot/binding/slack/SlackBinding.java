package org.wanna.jabbot.binding.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import flowctrl.integration.slack.SlackClientFactory;
import flowctrl.integration.slack.rtm.Event;
import flowctrl.integration.slack.rtm.EventListener;
import flowctrl.integration.slack.rtm.SlackRealTimeMessagingClient;
import flowctrl.integration.slack.webapi.SlackWebApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.messaging.TxMessage;

import java.util.Hashtable;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class SlackBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(SlackBinding.class);
	private boolean connected;
	private Hashtable<String,SlackRoom> roomMap = null;
	SlackRealTimeMessagingClient rtmClient;
	SlackWebApiClient webApiClient;
	long pingId = 0;

	public SlackBinding(BindingConfiguration configuration) {
		super(configuration);
		roomMap = new Hashtable<String,SlackRoom>();
	}

	@Override
	public boolean connect() {
		logger.info("Creating RTM Client");
		try {
			rtmClient = SlackClientFactory.createSlackRealTimeMessagingClient(getConfiguration().getPassword());
			webApiClient = SlackClientFactory.createWebApiClient(getConfiguration().getPassword());
		} catch (Throwable e) {
			logger.error("Unable to creat clients", e);
		}
		logger.info("RTM Client created! connecting");
		rtmClient.addListener(Event.MESSAGE, new EventListener() {
			@Override
			public void handleMessage(JsonNode jsonNode) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					flowctrl.integration.slack.type.Message slackMsg = mapper.treeToValue(jsonNode,flowctrl.integration.slack.type.Message.class);

					logger.info("Got a message: " + slackMsg.getText());

					for (SlackRoom sr : roomMap.values()) {
						String channelId = jsonNode.get("channel").asText();
						logger.debug("Comparing " + jsonNode.get("channel").asText() + " to " + sr.channelId);
						if(sr.channelId != null && sr.channelId.equals(channelId)) {
							logger.debug("Room found: dispatching");
							sr.dispatchMessage(slackMsg);
						}
					}
				} catch (JsonProcessingException e) {
					logger.error("Faled to parse message",e);
				}
			}
		});
		rtmClient.connect();
		logger.info("RTP Connected");

		connected = true;
		super.dispatchEvent(new ConnectedEvent(this));
		return connected;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		logger.debug("Joining room " + configuration.getName());
		SlackRoom room = new SlackRoom(this,listeners);
		roomMap.put(configuration.getName(),room);
		room.join(configuration);
		return room;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public Room getRoom(String roomName) {
		return roomMap.get(roomName);
	}

}
