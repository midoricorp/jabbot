package org.wanna.jabbot.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public class BindingMessage {
	private String body;
	private String sender;
	private String roomName;

	public BindingMessage(String roomName, String sender,String body) {
		this.roomName = roomName;
		this.body = body;
		this.sender = sender;
	}

	public String getBody() {
		return body;
	}

	public String getSender() {
		return sender;
	}

	public String getRoomName() {
		return roomName;
	}
}
