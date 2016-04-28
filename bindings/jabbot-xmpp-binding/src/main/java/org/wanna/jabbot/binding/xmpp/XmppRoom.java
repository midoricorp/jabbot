package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
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
import org.wanna.jabbot.binding.messaging.Message;

import java.util.Date;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class XmppRoom extends AbstractRoom<XmppBinding> {
	public final Logger logger = LoggerFactory.getLogger(XmppRoom.class);
	private MultiUserChat muc;
	private RoomConfiguration configuration;

	public XmppRoom(XmppBinding connection) {
		super(connection);
	}

	public boolean sendMessage(final Message message) {
		try {
            org.jivesoftware.smack.packet.Message xmppMessage = MessageHelper.createResponseMessage((XmppMessage)message);
			muc.sendMessage(xmppMessage);
			return true;
		} catch (SmackException.NotConnectedException e) {
			logger.error("error while sending message",e);
		}
		return false;
	}

	@Override
	public boolean join(RoomConfiguration configuration) {
		final int nickChangeAttempts = 5;
		this.configuration = configuration;
		muc = MultiUserChatManager.getInstanceFor(connection.getConnection()).getMultiUserChat(configuration.getName());
		String nickname = configuration.getNickname();
		int i = 0;
		while(i<nickChangeAttempts){
			try{
				DiscussionHistory history = new DiscussionHistory();
				history.setSince(new Date());
				muc.join(nickname,null,history,connection.getConnection().getPacketReplyTimeout());
				logger.info("[XMPP] joining room {}",configuration.getName());
				connection.dispatchEvent(new RoomJoinedEvent(connection,this));
				return true;
			}catch (XMPPException.XMPPErrorException e){
				logger.error("error condition",e.getXMPPError().getCondition());
				if(e.getXMPPError().getCondition().equals(XMPPError.Condition.conflict)){
					logger.debug("nickname already taken.. changing");
					nickname+=i;
					i++;
					configuration.setNickname(nickname);
				}
			} catch ( SmackException e) {
				logger.error("error while joining room", e);
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
