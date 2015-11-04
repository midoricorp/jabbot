package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.wanna.jabbot.binding.messaging.Resource;
import org.wanna.jabbot.binding.privilege.Privilege;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-09
 */
public class PrivilegeMapper {
    private XMPPConnection connection;

    public PrivilegeMapper(XMPPConnection connection) {
        this.connection = connection;
    }

    public Privilege getResourcePrivileges(Resource resource, Resource target){
        MultiUserChat chatroom = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(resource.getAddress());
        String occupantAddress = resource.getAddress()+"/"+resource.getName();
        Occupant o = chatroom.getOccupant(occupantAddress);
        switch(o.getRole()){
            case moderator: return Privilege.MODERATOR;
            default: return Privilege.USER;
        }
    }
}
