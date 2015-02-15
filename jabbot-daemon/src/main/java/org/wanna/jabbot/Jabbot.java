package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.ConnectionCreationException;
import org.wanna.jabbot.binding.ConnectionFactory;
import org.wanna.jabbot.binding.JabbotConnection;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.config.JabbotConfiguration;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private List<JabbotConnection> connectionList = new ArrayList<>();
	private ConnectionFactory connectionFactory;

	public Jabbot( JabbotConfiguration configuration ) {
		this.configuration = configuration;
	}

	public boolean connect(){
		for (JabbotConnectionConfiguration connectionConfiguration : configuration.getServerList()) {
			JabbotConnection conn;
			try {
				conn = connectionFactory.create(connectionConfiguration);
				conn.connect();
				for (RoomConfiguration roomConfiguration : connectionConfiguration.getRooms()) {
					conn.joinRoom(roomConfiguration);
				}
				connectionList.add(conn);
				if(conn.isConnected()){
					logger.debug("connection established to {} as {}",connectionConfiguration.getUrl(),connectionConfiguration.getUsername());
				}
			} catch (ConnectionCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
