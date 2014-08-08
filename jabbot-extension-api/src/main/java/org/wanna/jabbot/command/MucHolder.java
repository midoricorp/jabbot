package org.wanna.jabbot.command;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public interface MucHolder {
	String getNickname();
	String getRoomName();
	boolean sendMessage(String message);

}
