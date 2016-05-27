package org.wanna.jabbot.binding.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.messaging.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tsearle <tsearle>
 * @since 2016-03-08
 */
public class SparkRoom extends AbstractRoom<SparkBinding> implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(SparkRoom.class);
//	private RoomConfiguration configuration;
	private final List<BindingListener> listeners;
	private com.ciscospark.Room room;
	private com.ciscospark.Spark spark;
	private com.ciscospark.SparkServlet servlet;
	private boolean useWebhook;
	private String webhookUrl;
	private Pattern urlPattern = Pattern.compile("((http|https):[^\\s]+\\.(doc|docx|ppt|pptx|pdf|jpg|jpeg|png|gif|bmp))");


	public SparkRoom(SparkBinding connection,List<BindingListener> listeners) {
		super(connection);
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);
		spark = connection.spark;
		servlet = connection.sparkServlet;
		useWebhook = connection.useWebhook;
		webhookUrl = connection.webhookUrl;

	}

	@Override
	public void run()
	{
		logger.info("SPARK Room started");
		String lastId = null;
		while (true) {
			Stack<com.ciscospark.Message> msgList = new Stack<com.ciscospark.Message>();
			
			while (true) {
				String nextId = null;
				try {
					Iterator<com.ciscospark.Message> msgs = spark.messages()
						.queryParam("roomId", room.getId())
						.queryParam("max", "20")	

						.iterate();
					while(msgs.hasNext()) {
						com.ciscospark.Message imsg = msgs.next();

						if (lastId == null) {
							nextId = imsg.getId();
							break;
						}

						if (nextId == null) {
							nextId = imsg.getId();
						}

						if (lastId.equals(imsg.getId())) {
							//caugh up
							break;
						}

						logger.info("MessageContent Received (" + imsg.getPersonEmail() + ") " + imsg.getText());
						msgList.push(imsg);
					}

					lastId = nextId;

					if (!msgList.empty()) break;
				} catch (Exception e) {
					logger.error("Error in message handling!" , e);
				}
				try {
					Thread.sleep(5000);
				} catch(Exception e) {}
			}


			while(!msgList.empty()) {
				try {
					com.ciscospark.Message msg = msgList.pop();
					dispatchMessage(msg);
				}catch(Exception e){
					logger.error("error dispatching message {}",msgList,e);
				}
			}
		}
	}


	public boolean create(String roomId) {
		com.ciscospark.Room room  = spark.rooms().path("/"+roomId).get();
		if (room == null) {
			return false;
		}
		return create(room);
	}

	public boolean create(com.ciscospark.Room room) {

		this.room = room;

		if (!useWebhook) {
			new Thread(this).start();
		} else {
			com.ciscospark.Webhook hook = new com.ciscospark.Webhook();
			hook.setName("midori hook");
			hook.setTargetUrl(URI.create(webhookUrl));
			hook.setResource("messages");
			hook.setEvent("created");
			hook.setFilter("roomId=" + room.getId());
			hook = spark.webhooks().post(hook);
			logger.info("created webhook " + hook.getName() + " id: " + hook.getId() + " for room: " + room.getTitle());
			final String roomId = room.getId();
			servlet.addListener( new com.ciscospark.WebhookEventListener() {
				public void onEvent(com.ciscospark.WebhookEvent event) {
					com.ciscospark.Message msg = event.getData();
					if(event.getData().getRoomId().equals(roomId)) {
						if (msg.getText() == null) {
							logger.info("Getting full message for " + event.getData().getId());
							msg = spark.messages().path("/" + event.getData().getId()).get();
						} else {
							logger.info("MessageContent already in webhook, delivering");
						}
						dispatchMessage(msg);
					}
				}
			});
		}
		return true;
	}

	private void dispatchMessage(com.ciscospark.Message msg) {
		for (BindingListener listener : listeners) {
			RxMessage request = new DefaultRxMessage(new DefaultMessageContent(msg.getText()),
					new DefaultResource(room.getId(),msg.getPersonEmail()));
		    listener.eventReceived(new MessageEvent(this.connection,request));
		}
	}

	public boolean sendMessage(TxMessage response) {
		String body = response.getMessageContent().getBody();
		sendMessage(body);
		return true;
	}

	public void sendMessage(String body) {
		com.ciscospark.Message msg = new com.ciscospark.Message();
		msg.setRoomId(room.getId());
		msg.setText(body);
		Matcher m = urlPattern.matcher(body);
		if (m.find()) {
			msg.setFile(m.group(0));
		}
		spark.messages().post(msg);
	}

	@Override
	public boolean join(RoomConfiguration configuration) {
		return false;
	}

	@Override
	public String getRoomName() {
		return room.getTitle();
	}
}
