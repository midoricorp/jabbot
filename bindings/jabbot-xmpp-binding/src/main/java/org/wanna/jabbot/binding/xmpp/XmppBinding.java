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
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jxmpp.util.XmppStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.messaging.body.BodyPart;
import org.wanna.jabbot.command.messaging.body.BodyPartValidator;
import org.wanna.jabbot.command.messaging.body.BodyPartValidatorFactory;
import org.wanna.jabbot.command.messaging.body.InvalidBodyPartException;

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
				new OrFilter(MessageTypeFilter.GROUPCHAT,MessageTypeFilter.CHAT),
				new StanzaFilter() {
					@Override
					public boolean accept(Stanza stanza) {
						return stanza instanceof Message && ((Message) stanza).getBody().startsWith(prefix);
					}
				}
		);

		XmppMessageListener commandListener = new XmppMessageListener(listeners);
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

    @Override
    public void sendMessage(BindingMessage message) {
        if(! (message instanceof XmppMessage)) return;

        if(message.getRoomName() != null){
            Room room = getRoom(message.getRoomName());
            if(room != null){
                room.sendMessage(message);
            }
        }else{
            Chat chat = ChatManager.getInstanceFor(connection).getThreadChat(((XmppMessage)message).getThread());
            if(chat == null){
                logger.trace("chat was null, creating a new chat instance");

                chat = ChatManager.getInstanceFor(connection).createChat(message.getDestination(),((XmppMessage)message).getThread(),null);
            }else{
                logger.trace("an existing chat was found for thread id {}", ((XmppMessage) message).getThread());
            }
            try {
                Message msg = MessageHelper.createResponseMessage(((XmppMessage)message));
                msg.addExtension(new DeliveryReceipt(((XmppMessage)message).getId()));
                chat.sendMessage(msg);
            } catch (SmackException.NotConnectedException e) {
                logger.warn("Trying to send a message on XMPP binding while connection is closed",e);
            }
        }
    }

    @Override
    public org.wanna.jabbot.command.messaging.Message createResponseMessage(BindingMessage source, org.wanna.jabbot.command.messaging.Message eventResponse) {
        if(!(source instanceof XmppMessage)) return null;
        XmppMessage response = new XmppMessage();
        response.setId(((XmppMessage)source).getId());
        response.setSender(source.getDestination());
        response.setDestination(source.getSender());
        response.setRoomName(source.getRoomName());
        response.setThread(((XmppMessage)source).getThread());

        for (BodyPart body : eventResponse.getBodies()) {
            BodyPartValidator validator = BodyPartValidatorFactory.getInstance().create(body.getType());
            try {
                //If a validator exists for that body type, validate the message
                if(validator != null){
                    validator.validate(body);
                }
                response.addBody(body);
            } catch (InvalidBodyPartException e) {
                logger.info("discarding XhtmlBodyPart as it's content is declared invalid: {}"
                        ,(e.getInvalidBodyPart()==null?"NULL":e.getInvalidBodyPart().getText()));
            }
        }
        return response;
    }
}
