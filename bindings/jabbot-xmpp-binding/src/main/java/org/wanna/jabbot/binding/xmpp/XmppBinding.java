package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.util.XmppStringUtils;
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
public class XmppBinding extends AbstractBinding<XMPPTCPConnection> {
	private final Logger logger = LoggerFactory.getLogger(XmppBinding.class);
	private Map<String,Room> rooms = new HashMap<>();
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
			connection.login();
			PingManager.getInstanceFor(connection).setPingInterval(pingInterval);
			this.initListeners(configuration.getCommandPrefix(),connection);
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

	/**
	 * Initialize PacketListener for a given {@link org.jivesoftware.smack.XMPPConnection}
	 * and a Command prefix
	 *
	 * @param prefix the command prefix used to filter message
	 * @param connection the connection on which PacketListener will be registered
	 */
	private void initListeners(final String prefix, final XMPPConnection connection){
		StanzaFilter filter = new AndFilter(
				MessageTypeFilter.GROUPCHAT,
				new StanzaFilter() {
					@Override
					public boolean accept(Stanza stanza) {
						return stanza instanceof Message && ((Message) stanza).getBody().startsWith(prefix);
					}

				}
		);

		MucCommandListener commandListener = new MucCommandListener(this, listeners);
		connection.addAsyncStanzaListener(commandListener,filter);
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	private XMPPTCPConnectionConfiguration newConnectionConfiguration(final BindingConfiguration configuration){
		XMPPTCPConnectionConfiguration.Builder configurationBuilder = XMPPTCPConnectionConfiguration.builder();
		configurationBuilder.setHost(configuration.getUrl())
				.setPort(configuration.getPort())
				.setServiceName(configuration.getServerName())
				.setUsernameAndPassword(configuration.getUsername(),configuration.getPassword())
				.setResource(configuration.getIdentifier())
				.setDebuggerEnabled(configuration.isDebug());

		if(allowSelfSigned){
			try {
				configurationBuilder.setCustomSSLContext(SSLHelper.newAllTrustingSslContext());
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				logger.error("Error registering custom ssl context",e);
			}
		}

		return configurationBuilder.build();
	}

	@Override
	public Room getRoom(String roomName) {
		if(roomName == null ){
			return null;
		}
		return rooms.get(XmppStringUtils.parseBareJid(roomName));
	}
}
