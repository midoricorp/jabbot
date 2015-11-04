package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.util.XmppStringUtils;
import org.wanna.jabbot.binding.messaging.Resource;
import org.wanna.jabbot.binding.messaging.body.BodyPart;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;

/**
 * MessageHelper is a class which provide some facilities with regards to Xmpp message.
 *
 * @author Vincent Morsiani
 * @since 2015-07-22
 */
public final class MessageHelper {

    /**
     * List of characters we don't want to see in an XMPP message body
     */
    private final static char[] escapeChars = new char[]{'\f','\b'};

    /**
     * Do not allow instance creation of MessageHelper
     */
    private MessageHelper(){

    }

    /**
     * Create an XMPP Message out of a Jabbot Message
     * It will check for the presence of XHTML Body, and feed the XMPP message with it if present.
     * Otherwise, only the raw TEXT message will be fed.
     *
     * @param message Jabbot Message
     * @return Xmpp Message
     */
    public static org.jivesoftware.smack.packet.Message createResponseMessage(XmppMessage message){
        org.jivesoftware.smack.packet.Message xmppMessage = new org.jivesoftware.smack.packet.Message();
        //First set the message body to raw text
        String secured = message.getBody();
        for (char escapeChar : escapeChars) {
            secured = secured.replace(escapeChar,' ');
        }

        xmppMessage.setBody(secured);
        //Check if there's any XHTML body part, if yes, set it
        BodyPart xhtmlPart = message.getBody(BodyPart.Type.XHTML);
        if(xhtmlPart != null){
            XmlStringBuilder sb = new XmlStringBuilder();
            sb.append("<body>");
            sb.append(xhtmlPart.getText());
            sb.append("</body>");
            XHTMLExtension xhtmlExtension = XHTMLExtension.from(xmppMessage);
            if (xhtmlExtension == null) {
                // Create an XHTMLExtension and add it to the message
                xhtmlExtension = new XHTMLExtension();
                xmppMessage.addExtension(xhtmlExtension);
            }
            // Add the required bodies to the message
            xhtmlExtension.addBody(sb);
        }
        if(message.getDestination().getType().equals(Resource.Type.ROOM)){
            xmppMessage.setType(org.jivesoftware.smack.packet.Message.Type.groupchat);
            xmppMessage.setTo(message.getDestination().getAddress());
        }else{
            xmppMessage.setTo(message.getDestination().getAddress());
            xmppMessage.setThread(message.getThread());
            xmppMessage.setStanzaId(message.getId());
            xmppMessage.setType(org.jivesoftware.smack.packet.Message.Type.chat);
        }
        xmppMessage.setFrom(message.getSender().getAddress());
        return xmppMessage;
    }

    public static XmppMessage createRequestMessage(org.jivesoftware.smack.packet.Message xmppMessage){
        XmppMessage msg = new XmppMessage();
        msg.setId(xmppMessage.getStanzaId());
        msg.addBody(new TextBodyPart(xmppMessage.getBody()));
        msg.setDestination(new XmppResource(xmppMessage.getTo(),null));
        msg.setThread(xmppMessage.getThread());

        if(xmppMessage.getType().equals(org.jivesoftware.smack.packet.Message.Type.groupchat)){
            msg.setSender(
                    new XmppResource(
                            XmppStringUtils.parseBareJid(xmppMessage.getFrom()),
                            XmppStringUtils.parseResource(xmppMessage.getFrom()),
                            Resource.Type.ROOM
                    ));
            //msg.setRoomName();

        }else{
            msg.setSender(new XmppResource(xmppMessage.getFrom(),null, Resource.Type.USER));
        }
        return msg;
    }
}
