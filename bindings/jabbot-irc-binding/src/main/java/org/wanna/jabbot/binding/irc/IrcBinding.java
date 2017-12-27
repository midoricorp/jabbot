package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.Callback;
import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.IRCApiImpl;
import com.ircclouds.irc.api.IServerParameters;
import com.ircclouds.irc.api.domain.IRCServer;
import com.ircclouds.irc.api.state.IIRCState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.ConnectionException;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class IrcBinding extends AbstractBinding<IRCApi> {
	private final Logger logger = LoggerFactory.getLogger(IrcBinding.class);

	private Map<String,Room> rooms = new HashMap<>();
	private IIRCState state;

	public IrcBinding(BindingConfiguration configuration) {
		super(configuration);
	}

	@Override
	public boolean connect() {
		connection = new IRCApiImpl(true);
		IrcMessageListener listener = new IrcMessageListener(this,listeners);
		connection.addListener(listener);

		ConnectionCallback connectionCallback = new ConnectionCallback(this);
		IServerParameters parameters = getServerParameters(getConfiguration());
		connection.connect(parameters, connectionCallback );

		return true;
	}

	@Override
	public boolean disconnect() {
		connection.disconnect();
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		Room room = new IrcRoom(this,connection);
		room.join(configuration);
		rooms.put("#" + room.getRoomName(), room);

		return room;
	}

	private IServerParameters getServerParameters(final BindingConfiguration configuration){
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
		private IrcBinding binding;

		public ConnectionCallback(IrcBinding binding) {
			this.binding = binding;
		}

		@Override
		public void onSuccess(IIRCState aObject) {
			state = aObject;
			binding.dispatchEvent(new ConnectedEvent(binding));
		}

		@Override
		public void onFailure(Exception aExc) {
			throw new ConnectionException(aExc);
		}
	}

	@Override
	public boolean isConnected() {
		if(state == null){
			return false;
		}
		//TODO : this is a way to work around the fact that IRCState object don't get refreshed
		try {
			connection.rawMessage("PING " + getConfiguration().getUsername());
			return true;
		}catch (Exception e){
			return false;
		}
	}

	@Override
	public Room getRoom(String roomName) {
		if(roomName==null){
			return null;
		}
		return rooms.get(roomName);
	}
}
