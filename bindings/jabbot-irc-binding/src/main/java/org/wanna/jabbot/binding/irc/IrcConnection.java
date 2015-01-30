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
import org.wanna.jabbot.command.parser.DefaultCommandParser;

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
	public boolean connect() {
		final JabbotConnectionConfiguration configuration = super.getConfiguration();
		connection = new IRCApiImpl(false);
		Callback<IIRCState> callback = new Callback<IIRCState>(){
			@Override
			public void onSuccess(IIRCState aObject) {
				joinChannels(configuration.getRooms());
			}

			@Override
			public void onFailure(Exception aExc) {

			}
		};
		IServerParameters parameters = getServerParameters(configuration);
		connection.connect(parameters, callback );
		RoomListener listener = new RoomListener(commandFactory,new DefaultCommandParser(configuration.getCommandPrefix()));
		listener.setRooms(rooms);
		connection.addListener(listener);
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		Room room = new IrcRoom(this);
		room.join(configuration);
		rooms.put("#" + room.getRoomName(), room);

		return room;
	}

	private void joinChannels(final List<RoomConfiguration> roomConfigurations){
		for (RoomConfiguration roomConfiguration : roomConfigurations) {
			joinRoom(roomConfiguration);
		}
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

	@Override
	public boolean isConnected() {
		return true;
	}
}
