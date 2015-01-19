package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.ping.PingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractJabbotConnection;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.parser.DefaultCommandParser;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public class XmppConnection extends AbstractJabbotConnection<XMPPConnection> {
	private final Logger logger = LoggerFactory.getLogger(XmppConnection.class);
	private Map<String,Room> rooms = new HashMap<>();
	private MucCommandListener commandListener;

	public XmppConnection(JabbotConnectionConfiguration configuration) {
		super(configuration);
	}

	@Override
	public boolean connect(JabbotConnectionConfiguration configuration) {

		connection = new XMPPTCPConnection(newConnectionConfiguration(configuration));
		try {
			connection.connect();
			connection.login(configuration.getUsername(),configuration.getPassword(),configuration.getIdentifier());
			PingManager.getInstanceFor(connection).setPingInterval(3000);
			this.initListeners(connection);
			this.joinRooms(configuration.getRooms());
			return connection.isConnected();
		} catch (XMPPException | SmackException | IOException e) {
			logger.error("error while connecting",e);
		}
		return false;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		Room room = new XmppRoom(this);
		room.join(configuration);
		rooms.put(configuration.getName(),room);

		return room;
	}

	private void joinRooms(List<RoomConfiguration> roomConfigurationList){
		for (RoomConfiguration configuration : roomConfigurationList) {
			joinRoom(configuration);
		}
	}

	private void initListeners(XMPPConnection connection){
		final String prefix = getConfiguration().getCommandPrefix();
		PacketFilter filter = new AndFilter(
				new MessageTypeFilter(Message.Type.groupchat),
				new PacketFilter() {
					@Override
					public boolean accept(Packet packet) {
						return packet instanceof Message && ((Message) packet).getBody().startsWith(prefix);
					}
				}
		);

		commandListener = new MucCommandListener();
		commandListener.setCommandFactory(commandFactory);
		commandListener.setCommandParser(new DefaultCommandParser(prefix));
		commandListener.setRooms(this.rooms);
		connection.addPacketListener(commandListener,filter);
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	private ConnectionConfiguration newConnectionConfiguration(final JabbotConnectionConfiguration configuration){
		ConnectionConfiguration config = new ConnectionConfiguration(
				configuration.getUrl(),configuration.getPort(),configuration.getServerName()
		);
		config.setDebuggerEnabled(configuration.isDebug());
		//TODO : push param below to configuration
		boolean allowSelfSigned = true;
		if(allowSelfSigned){
			try {
				config.setCustomSSLContext(SSLHelper.newAllTrustingSslContext());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				logger.error("Error registering custom ssl context",e);
			}
		}

		return config;
	}

}
