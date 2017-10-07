package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.RoomJoinedEvent;
import org.wanna.jabbot.messaging.TxMessage;

import java.util.Date;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class XmppRoom extends AbstractRoom<XmppBinding> {
	public final Logger logger = LoggerFactory.getLogger(XmppRoom.class);
	private MultiUserChat muc;
	private RoomConfiguration configuration;
	private XMPPConnection xmppConnection;

	public XmppRoom(XmppBinding connection, XMPPConnection xmppConnection) {
		super(connection);
		this.xmppConnection = xmppConnection;
	}

	public boolean sendMessage(final TxMessage response){
		org.jivesoftware.smack.packet.Message xmpp = MessageHelper.createXmppMessage(response);
		try {
			muc.sendMessage(xmpp);
			return true;
		} catch (SmackException.NotConnectedException e) {
			return false;
		}
	}

	@Override
	public boolean join(RoomConfiguration configuration) {
		final int nickChangeAttempts = 5;
		this.configuration = configuration;
		muc = MultiUserChatManager.getInstanceFor(xmppConnection).getMultiUserChat(configuration.getName());
		String nickname = configuration.getNickname();
		int i = 0;
		while(i<nickChangeAttempts){
			try{
				DiscussionHistory history = new DiscussionHistory();
				history.setSince(new Date());
				muc.join(nickname,null,history,xmppConnection.getPacketReplyTimeout());
				logger.info("{} - joining room {}",getConnection().getIdentifier(),configuration.getName());
				connection.dispatchEvent(new RoomJoinedEvent(connection,this));
				return true;
			}catch (XMPPException.XMPPErrorException e){
				logger.error("{} - error condition",getConnection().getIdentifier(),e.getXMPPError().getCondition());
				if(e.getXMPPError().getCondition().equals(XMPPError.Condition.conflict)){
					logger.debug("{} - nickname already taken.. changing",getConnection().getIdentifier());
					nickname+=i;
					i++;
					configuration.setNickname(nickname);
				}
			} catch ( SmackException e) {
				logger.error("{} - error while joining room", getConnection().getIdentifier(),e);
				return false;
			}
		}
		return false;
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}

	public RoomConfiguration getConfiguration() {
		return configuration;
	}

	public XmppBinding getConnection() {
		return connection;
	}
}
