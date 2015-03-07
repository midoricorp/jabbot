package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-03-06
 */
public interface CommandMessage {
	String getBody();
	String getSender();
	String getRoomName();
}
