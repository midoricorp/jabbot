package org.wanna.jabbot.binding.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.messaging.*;
import org.wanna.jabbot.messaging.body.BodyPart;

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
	private boolean running;
	private Pattern urlPattern = Pattern.compile("((http|https):[^\\\\s]+\\.(doc|docx|ppt|pptx|pdf|jpg|jpeg|png|gif|bmp))");


	public SparkRoom(SparkBinding connection,List<BindingListener> listeners) {
		super(connection);
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);
		spark = connection.spark;
		servlet = connection.sparkServlet;
		useWebhook = connection.useWebhook;
		webhookUrl = connection.webhookUrl;

	}

	class SparkResource implements Resource {

		String address;
		String user;
		String parentId;

		public SparkResource(String address, String parentId, String user) {
			this.address = address;
			this.user = user;
			this.parentId = parentId;
		}

		@Override
		public String getAddress() {
			return address;
		}

		@Override
		public String getName() {
			return user;
		}

		@Override
		public Type getType() {
			return Type.ROOM;
		}

		public String getParentId() { return parentId; }
	}


	@Override
	public void run()
	{
		logger.info("SPARK Room started");
		String lastId = null;
		while (running) {
			Stack<com.ciscospark.Message> msgList = new Stack<com.ciscospark.Message>();
			
			while (running) {
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
							//caught up
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
			running=true;
			new Thread(this).start();
		}
		return true;
	}

	void dispatchMessage(com.ciscospark.Message msg) {
		if (connection.me != null && msg.getId().equals(connection.me.getId())) {
			logger.info("Ignoring message from myself!");
			return;
		}
		for (BindingListener listener : listeners) {
			String html = msg.getHtml();
			String text = null;
			if (html != null) {
				logger.info("About reformat html: " + html);
				html = html.replace("<code>","\n<code>");
				HtmlReformat he = new HtmlReformat(connection.me, html);
				text = he.removeMentions();
			} else {
				text = msg.getText();
				if (connection.me != null) {
					text = text.replace(connection.me.getDisplayName(), "").trim();
				}
			}
			logger.info("Got a message of: " + text);
			RxMessage request = new DefaultRxMessage(new DefaultMessageContent(text),
					new SparkResource(room.getId(),msg.getParentId(),msg.getPersonEmail()));
		    listener.eventReceived(new MessageEvent(this.connection,request));
		}
	}

	public boolean sendMessage(TxMessage response) {
		String body = response.getMessageContent().getBody();
		BodyPart html = response.getMessageContent().getBody(BodyPart.Type.XHTML);
		SparkResource resource = (SparkResource)response.getDestination();
		if (html == null) {
			sendMessage(resource.getParentId(), body, null);
		} else {
			String htmlTxt = html.getText();
			HtmlReformat he = new HtmlReformat(connection.me, htmlTxt);
			htmlTxt = he.emojiify();
			sendMessage(resource.getParentId(), body, htmlTxt);
		}
		return true;
	}

	public void sendMessage(String parentID, String body, String html) {
		com.ciscospark.Message msg = new com.ciscospark.Message();
		msg.setRoomId(room.getId());
		msg.setText(body);

		if(parentID != null) {
			msg.setParentId(parentID);
		}

		if (html != null) {
			html = html.replace("\n","");
			msg.setHtml(html);

			String file = HtmlReformat.findImage(html);
			if (file != null) {
				msg.setFile(file);
			}
		} else {
			Matcher m = urlPattern.matcher(body);
			if (m.find()) {
				msg.setFile(m.group(0));
			}
		}
		logger.info("Sending Message (file): " + msg.getFile());
		logger.info("Sending Message (txt): " + msg.getText());
		logger.info("Sending Message (html): " + msg.getHtml());
		spark.messages().post(msg);
	}

	@Override
	public boolean join(RoomConfiguration configuration) {
		return false;
	}

	@Override
	public void leave() {
		running = false;
	}

	@Override
	public String getRoomName() {
		return room.getTitle();
	}
}
