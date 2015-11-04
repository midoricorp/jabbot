package org.wanna.jabbot.binding.xmpp.command;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.privilege.Privilege;
import org.wanna.jabbot.binding.privilege.PrivilegedAction;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;

import java.util.List;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-04
 */
public class KickCommand extends XmppCommand implements PrivilegedAction{

    public KickCommand(CommandConfig config) {
        super(config);
    }

    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public String getDescription() {
        return "command used to kick people out of chatroom";
    }

    @Override
    public String getHelpMessage() {
        return null;
    }

    @Override
    public Message process(CommandMessage message) {
        List<String> args = getArgsParser().parse(message.getBody());
        if(args.size() < 1){
            return new DefaultCommandMessage("Who should I kick?");
        }

        MultiUserChat chatroom = MultiUserChatManager.getInstanceFor(binding.getConnection()).getMultiUserChat(message.getSender().getAddress());

        try {
            chatroom.kickParticipant(args.get(0),"no reason");
            return new DefaultCommandMessage("job done!");
        } catch (XMPPException.XMPPErrorException e){
            return new DefaultCommandMessage("could not kick: "+e.getMessage());
        } catch(SmackException.NoResponseException | SmackException.NotConnectedException e) {
            return new DefaultCommandMessage("unexpected error occured");
        }

    }

    @Override
    public Privilege getRequiredPrivilege() {
        return Privilege.MODERATOR;
    }
}
