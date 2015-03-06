package org.wanna.jabbot.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-07
 */
public class MessageWrapper {
	private String body;
	private String roomName;
	private String sender;
	private List<String> args = new ArrayList<>();

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}
}
