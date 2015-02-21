package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.config.RoomConfiguration;

import java.util.Date;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class XmppRoom extends AbstractRoom<XmppBinding> {
	public final Logger logger = LoggerFactory.getLogger(XmppRoom.class);
	//escape characters which would cause smack to crash
	private final char[] escapeChars = new char[]{'\f','\b'};
	private MultiUserChat muc;
	private RoomConfiguration configuration;

	public XmppRoom(XmppBinding connection) {
		super(connection);
	}

	@Override
	public RoomConfiguration getConfiguration() {
		return configuration;
	}

	public boolean sendMessage(final String message) {
		try {
			String secured = message;
			for (char escapeChar : escapeChars) {
				secured = secured.replace(escapeChar,' ');
			}
			logger.debug("sending message: {}",secured);
			muc.sendMessage(secured);
			return true;
		} catch (XMPPException | SmackException.NotConnectedException e) {
			logger.error("error while sending message",e);
		}
		return false;
	}

	@Override
	public boolean join(RoomConfiguration configuration) {
		this.configuration = configuration;
		muc = new MultiUserChat(connection.getWrappedConnection(),configuration.getName());
		String nickname = configuration.getNickname();
		int i = 0;

		try{
			DiscussionHistory history = new DiscussionHistory();
			history.setSince(new Date());
			muc.join(nickname,null,history,connection.getWrappedConnection().getPacketReplyTimeout());
			logger.info("[XMPP] joining room {}",configuration.getName());
		}catch (XMPPException.XMPPErrorException e){
			logger.error("error condition",e.getXMPPError().getCondition());
			if(e.getXMPPError().getCondition().equals(XMPPError.Condition.conflict.toString())){
				logger.debug("nickname already taken.. changing");
				nickname+=i;
				i++;
				configuration.setNickname(nickname);
			}
		} catch ( SmackException e) {
			logger.error("error while joining room", e);
		}

		return false;
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}

	public XmppBinding getConnection() {
		return connection;
	}
}
