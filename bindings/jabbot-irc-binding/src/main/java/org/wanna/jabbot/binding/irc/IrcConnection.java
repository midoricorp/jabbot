package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.Callback;
import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.IRCApiImpl;
import com.ircclouds.irc.api.IServerParameters;
import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.state.IIRCState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractJabbotConnection;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class IrcConnection extends AbstractJabbotConnection<IRCApi> {
	private final Logger logger = LoggerFactory.getLogger(IrcConnection.class);

	private Map<String,Room> rooms = new HashMap<>();

	public IrcConnection(JabbotConnectionConfiguration configuration) {
		super(configuration);
	}

	@Override
	public boolean connect(JabbotConnectionConfiguration configuration) {
		connection = new IRCApiImpl(false);
		RoomListener listener = new RoomListener(this,listeners);
		connection.addListener(listener);

		ConnectionCallback connectionCallback = new ConnectionCallback();
		IServerParameters parameters = getServerParameters(configuration);
		connection.connect(parameters, connectionCallback );

		while(!connectionCallback.isConnected()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		Room room = new IrcRoom(this);
		room.join(configuration);
		rooms.put("#" + room.getRoomName(), room);

		return room;
	}

	private IServerParameters getServerParameters(final JabbotConnectionConfiguration configuration){
		return new IServerParameters() {
			@Override
			public String getNickname() {
				return configuration.getUsername();
			}

			@Override
			public List<String> getAlternativeNicknames() {
				return Arrays.asList(configuration.getUsername());
			}

			@Override
			public String getIdent() {
				return configuration.getIdentifier();
			}

			@Override
			public String getRealname() {
				return configuration.getUsername();
			}

			@Override
			public IRCServer getServer() {
				return new IRCServer(configuration.getUrl(),false);
			}
		};

	}

	class ConnectionCallback implements Callback<IIRCState>{
		private boolean connected = false;

		@Override
		public void onSuccess(IIRCState aObject) {
			logger.info("[IRC] connection established on {}",aObject.getServer().getHostname());
			connected = aObject.isConnected();
		}

		@Override
		public void onFailure(Exception aExc) {

		}

		public boolean isConnected() {
			return connected;
		}
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Room getRoom(String roomName) {
		if(roomName==null){
			return null;
		}
		return rooms.get(roomName);
	}
}
