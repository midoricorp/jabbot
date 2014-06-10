package org.wanna.jabbot;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);
	private XMPPConnection connection;
	private String username,password,resource;
	private Map<String,Chatroom> chatroomList;

	public Jabbot(XMPPConnection connection) {
		this.connection = connection;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public void setChatroomList(Map<String,Chatroom> chatroomList) {
		this.chatroomList = chatroomList;
	}

	public boolean connect() throws IOException, SmackException, XMPPException {
		connection.connect();
		connection.login(username,password,resource);

		PacketFilter filter = new AndFilter(
				new MessageTypeFilter(Message.Type.groupchat),
				new PacketFilter() {
					@Override
					public boolean accept(Packet packet) {
						return packet instanceof Message && ((Message) packet).getBody().startsWith("!");
					}
				}
		);

		//connection.addPacketListener(new MucCommandListener(chatroomList),filter);
		return true;
	}

	public boolean initRooms() throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException, InterruptedException {
		for (Chatroom chatroom : chatroomList.values()) {
			chatroom.init(connection);
		}
		return true;
	}
}
