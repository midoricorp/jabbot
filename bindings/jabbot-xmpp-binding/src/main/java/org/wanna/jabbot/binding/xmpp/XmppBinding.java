package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jxmpp.util.XmppStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.*;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.messaging.Resource;
import org.wanna.jabbot.messaging.TxMessage;
import org.wanna.jabbot.binding.privilege.Privilege;
import org.wanna.jabbot.binding.privilege.PrivilegeGranter;
import org.wanna.jabbot.binding.privilege.PrivilegedAction;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-08
 */
public class XmppBinding extends AbstractBinding<XMPPTCPConnection> implements PrivilegeGranter {
	private final Logger logger = LoggerFactory.getLogger(XmppBinding.class);
	private Map<String,Room> rooms = new HashMap<>();
	private int pingInterval = 3000;
	private boolean allowSelfSigned = false;
    private PrivilegeMapper privilegeMapper;

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
	public boolean connect() {
		connection = new XMPPTCPConnection(newConnectionConfiguration(getConfiguration()));
        privilegeMapper = new PrivilegeMapper(connection);
		try {
			connection.connect();
			connection.login();
			PingManager.getInstanceFor(connection).setPingInterval(pingInterval);
			this.initListeners(getConfiguration().getCommandPrefix(),connection);
			super.dispatchEvent(new ConnectedEvent(this));
			return connection.isConnected();
		} catch (XMPPException | SmackException | IOException e) {
			logger.error("error while connecting",e);
		}
		return false;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		Room room = new XmppRoom(this,connection);
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
				new OrFilter(MessageTypeFilter.GROUPCHAT,MessageTypeFilter.CHAT),
				new StanzaFilter() {
					@Override
					public boolean accept(Stanza stanza) {
						return stanza instanceof Message && ((Message) stanza).getBody().startsWith(prefix);
					}
				}
		);

		XmppMessageListener commandListener = new XmppMessageListener(this,listeners);
		connection.addAsyncStanzaListener(commandListener,filter);
		MultiUserChatManager.getInstanceFor(connection).addInvitationListener(new InvitationListener(this,listeners));
	}

	@Override
	public boolean isConnected() {
		return (connection == null ? false :connection.isConnected());
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

    @Override
    public boolean canExecute(Resource resource, Resource target, PrivilegedAction action) {
        Privilege resourcePrivilege = privilegeMapper.getResourcePrivileges(resource,target);
        logger.debug("resource privilege: {} required privilege {}",resourcePrivilege,action.getRequiredPrivilege());
        int i = resourcePrivilege.compareTo(action.getRequiredPrivilege());//action.getRequiredPrivilege().compareTo(resourcePrivilege);
        boolean allowed = i >= 0;
        logger.debug("privilege check status: {} and thus is allowed: {}",i, allowed);
        return allowed;
    }

	@Override
	public void sendMessage(TxMessage message) {
		if(message.getDestination().getType().equals(Resource.Type.ROOM)){
			Room room = getRoom(message.getDestination().getAddress());
			if(room != null) {
				room.sendMessage(message);
			}
		}else{
			XmppRxMessage origin = (XmppRxMessage)message.getRequest();
			Chat chat = ChatManager.getInstanceFor(connection).getThreadChat(origin.getThread());
			if(chat == null){
				logger.trace("chat was null, creating a new chat instance");

				chat = ChatManager.getInstanceFor(connection).createChat(message.getDestination().getAddress(),origin.getThread(),null);
			}else{
				logger.trace("an existing chat was found for thread id {}", (origin.getThread()));
			}
			try {
				Message msg = MessageHelper.createXmppMessage(message);
				msg.addExtension(new DeliveryReceipt(origin.getId()));
				chat.sendMessage(msg);
			} catch (SmackException.NotConnectedException e) {
				logger.warn("Trying to send a message on XMPP binding while connection is closed",e);
			}

		}
	}
}
