package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.RoomInviteEvent;

import java.util.List;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class InvitationListener implements org.jivesoftware.smackx.muc.InvitationListener{
	private final Binding binding;
	private final List<BindingListener> listeners;

	public InvitationListener(Binding binding, List<BindingListener> listeners){
		this.binding = binding;
		this.listeners = listeners;
	}
	@Override
	public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
		RoomConfiguration configuration = new RoomConfiguration();
		configuration.setName(room.getRoom());
		configuration.setNickname(binding.getConfiguration().getUsername());
		RoomInviteEvent event = new RoomInviteEvent(binding,configuration);
		for (BindingListener listener : listeners) {
			listener.eventReceived(event);
		}
	}
}
