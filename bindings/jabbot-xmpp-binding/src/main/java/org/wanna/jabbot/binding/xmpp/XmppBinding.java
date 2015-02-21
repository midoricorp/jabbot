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
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.ping.PingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public class XmppBinding extends AbstractBinding<XMPPConnection> {
	private final Logger logger = LoggerFactory.getLogger(XmppBinding.class);
	private Map<String,Room> rooms = new HashMap<>();
	private MucCommandListener commandListener;
	private int pingInterval = 3000;
	private boolean allowSelfSigned = false;

	public XmppBinding(BindingConfiguration configuration) {
		super(configuration);
		if(configuration.getParameters() != null){
			if(configuration.getParameters().containsKey("ping_interval")){
				pingInterval = (int)configuration.getParameters().get("ping_interval");
			}
			if(configuration.getParameters().containsKey("allow_self_signed")){
				allowSelfSigned = (boolean)configuration.getParameters().get("allow_self_signed");
			}
		}
	}

	@Override
	public boolean connect(BindingConfiguration configuration) {
		connection = new XMPPTCPConnection(newConnectionConfiguration(configuration));
		try {
			connection.connect();
			connection.login(configuration.getUsername(),configuration.getPassword(),configuration.getIdentifier());
			PingManager.getInstanceFor(connection).setPingInterval(pingInterval);
			this.initListeners(connection);
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

		commandListener = new MucCommandListener(this,listeners);
		connection.addPacketListener(commandListener,filter);
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	private ConnectionConfiguration newConnectionConfiguration(final BindingConfiguration configuration){
		ConnectionConfiguration config = new ConnectionConfiguration(
				configuration.getUrl(),configuration.getPort(),configuration.getServerName()
		);
		config.setDebuggerEnabled(configuration.isDebug());
		if(allowSelfSigned){
			try {
				config.setCustomSSLContext(SSLHelper.newAllTrustingSslContext());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				logger.error("Error registering custom ssl context",e);
			}
		}

		return config;
	}

	@Override
	public Room getRoom(String roomName) {
		if(roomName == null ){
			return null;
		}
		return rooms.get(StringUtils.parseBareAddress(roomName));
	}
}
