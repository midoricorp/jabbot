package org.wanna.jabbot.binding.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import java.net.URI;
import java.util.Hashtable;

/**
 * @author tsearle <tsearle>
 * @since 2016-04-08
 */
public class SparkBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(SparkBinding.class);
	private Hashtable<String,Room> roomMap = null;
	com.ciscospark.Spark spark = null;

	public SparkBinding(BindingConfiguration configuration) {
		super(configuration);
		roomMap = new Hashtable<String,Room>();
	}

	@Override
	public boolean connect(BindingConfiguration configuration) {
		spark = com.ciscospark.Spark.builder()
			.baseUrl(URI.create(configuration.getUrl()))
			.accessToken(configuration.getPassword())
			.build();
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		logger.debug("Joining room " + configuration.getName());
		Room room = new SparkRoom(this,listeners);
		roomMap.put(configuration.getName(),room);
		room.join(configuration);
		return room;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Room getRoom(String roomName) {
		return roomMap.get(roomName);
	}
}
