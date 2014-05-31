package org.wanna.jabbot;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MucHolder;

import java.util.Date;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Chatroom implements MucHolder{
	final Logger logger = LoggerFactory.getLogger(Chatroom.class);

	private String nickname;
	private String roomName;
	private MultiUserChat muc;

	public Chatroom(String nickname, String roomName) {
		this.nickname = nickname;
		this.roomName = roomName;
	}

	public void init(XMPPConnection connection) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, InterruptedException {
		logger.debug("init room {}", roomName);
		muc = new MultiUserChat(connection,roomName);
		//this.initListeners(muc);

		int i = 0;
		while(!muc.isJoined()){
			try{
				DiscussionHistory history = new DiscussionHistory();
				history.setSince(new Date());
				muc.join(nickname,null,history,connection.getPacketReplyTimeout());

			}catch (XMPPException.XMPPErrorException e){
				logger.error("error condition",e.getXMPPError().getCondition());
				if(e.getXMPPError().getCondition().equals(XMPPError.Condition.conflict.toString())){
					logger.debug("nickname already taken.. changing");
					nickname+=i;
					i++;
				}else{
					throw e;
				}
			}
		}
	}

	public MultiUserChat getMuc() {
		return muc;
	}

	private void initListeners(final MultiUserChat muc){
		muc.addMessageListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) throws SmackException.NotConnectedException {
				if(packet instanceof Message){
					Message message = (Message)packet;
					if(true){
						//Occupant o = chatroom.getMuc().getOccupant(message.getFrom());
						logger.debug("chatroom message from {} received : {}",message.getFrom(),message.getBody());
						Occupant o  = muc.getOccupant(message.getFrom());
						if(o != null && o.getNick() != null && o.getNick().equals(muc.getNickname())){
							logger.debug("it's me saying that!");
						}else{
							Message response = new Message(StringUtils.parseBareAddress(message.getFrom()));
							response.setType(Message.Type.groupchat);
							response.setBody("!test");
							try {
								muc.sendMessage(response);
							} catch (XMPPException e) {
								logger.error("cannot send message",e);
							}
						}
					}
				}
			}
		});
	}

	public String getNickname() {
		return muc.getNickname();
	}

	public String getRoomName() {
		return roomName;
	}

	public void sendMessage(Message message) throws XMPPException, SmackException.NotConnectedException {
		muc.sendMessage(message);
	}
}
