package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-21
 */
public class DefaultCommandMessage implements CommandMessage{
	private String body;
	private String sender;
	private String roomName;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
