package org.wanna.jabbot.binding.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.messaging.DefaultResource;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.DefaultBindingMessage;
import org.wanna.jabbot.binding.messaging.body.BodyPart;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author tsearle <tsearle>
 * @since 2016-03-08
 */
public class SparkRoom extends AbstractRoom<Object> implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(SparkRoom.class);
	private RoomConfiguration configuration;
	private final List<BindingListener> listeners;
	private com.ciscospark.Room room;
	private com.ciscospark.Spark spark;


	public SparkRoom(SparkBinding connection,List<BindingListener> listeners) {
		super(connection);
		this.listeners = (listeners==null?new ArrayList<BindingListener>() : listeners);
		spark = connection.spark;

	}

	@Override
	public void run()
	{
		System.out.println("SPARK Room started");
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

						System.err.println(imsg.getPersonEmail() + ": " + imsg.getText());
						msgList.push(imsg);
					}

					lastId = nextId;

					if (!msgList.empty()) break;
				} catch (com.ciscospark.SparkException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(5000);
				} catch(Exception e) {}
			}


			while(!msgList.empty()) {
				com.ciscospark.Message msg = msgList.pop();
				for (BindingListener listener : listeners) {
				    DefaultBindingMessage message = new DefaultBindingMessage();
				    message.addBody(new TextBodyPart(msg.getText()));
				    message.setSender(new DefaultResource(this.getRoomName(),msg.getPersonEmail()));
				    message.setDestination(new DefaultResource(this.getRoomName(),null));
				    message.setRoomName(this.getRoomName());
				    listener.onMessage(message);
				}
			}
		}
	}

	@Override
	public boolean sendMessage(Message message) {
		com.ciscospark.Message msg = new com.ciscospark.Message();
		msg.setRoomId(room.getId());
		msg.setText(message.getBody());
		spark.messages().post(msg);
		return true;
	}

	@Override
	public boolean join(final RoomConfiguration configuration) {
		this.configuration = configuration;

		Iterator<com.ciscospark.Room> rooms = spark.rooms().iterate();
		while(rooms.hasNext()) {
			com.ciscospark.Room rm = rooms.next();
			System.err.println("comparing room " + rm.getTitle() 
					+ " to " + configuration.getName());
			if(rm.getTitle().equals(configuration.getName())) {
				room = rm;
				break;
			}
		}

		if (room == null) {
			System.err.println("Room " + configuration.getName() + " not found. Creating!");
			room = new com.ciscospark.Room();
			room.setTitle(configuration.getName());
			room = spark.rooms().post(room);
			System.err.println("Room created id: " + room.getId());
		}
		new Thread(this).start();
		return true;
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}
}
