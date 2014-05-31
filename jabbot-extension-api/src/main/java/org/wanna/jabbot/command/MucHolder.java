package org.wanna.jabbot.command;

import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public interface MucHolder {
	MultiUserChat getMuc();
	String getNickname();
	String getRoomName();

}
