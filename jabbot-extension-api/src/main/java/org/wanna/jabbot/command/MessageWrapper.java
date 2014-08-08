package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-07
 */
public class MessageWrapper {
	private Object wrappedMessage;
	private String body;
	private String sender;

	public MessageWrapper(Object wrappedMessage) {
		this.wrappedMessage = wrappedMessage;
	}

	public Object getWrappedMessage() {
		return wrappedMessage;
	}

	public void setWrappedMessage(Object wrappedMessage) {
		this.wrappedMessage = wrappedMessage;
	}

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
}
