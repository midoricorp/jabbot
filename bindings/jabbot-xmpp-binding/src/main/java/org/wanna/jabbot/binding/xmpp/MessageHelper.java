package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.util.XmppStringUtils;
import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.MessageContent;
import org.wanna.jabbot.binding.messaging.Resource;
import org.wanna.jabbot.binding.messaging.TxMessage;
import org.wanna.jabbot.binding.messaging.body.BodyPart;

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

    public static Message createXmppMessage(TxMessage txMessage){
        org.jivesoftware.smack.packet.Message xmpp = new org.jivesoftware.smack.packet.Message();
        MessageContent messageContent = txMessage.getMessageContent();
        String secured = messageContent.getBody();

        for (char escapeChar : escapeChars) {
            secured = secured.replace(escapeChar,' ');
        }
        xmpp.setBody(secured);
        //Check if there's any XHTML body part, if yes, set it
        BodyPart xhtmlPart = messageContent.getBody(BodyPart.Type.XHTML);
        if(xhtmlPart != null){
            XmlStringBuilder sb = new XmlStringBuilder();
            sb.append("<body>");
            sb.append(xhtmlPart.getText());
            sb.append("</body>");
            XHTMLExtension xhtmlExtension = XHTMLExtension.from(xmpp);
            if (xhtmlExtension == null) {
                // Create an XHTMLExtension and add it to the messageContent
                xhtmlExtension = new XHTMLExtension();
                xmpp.addExtension(xhtmlExtension);
            }
            // Add the required bodies to the messageContent
            xhtmlExtension.addBody(sb);
        }
        xmpp.setTo(txMessage.getDestination().getAddress());
        return xmpp;
    }

    public static XmppRxMessage createRxMessage(org.jivesoftware.smack.packet.Message xmppMessage){
        MessageContent content = new DefaultMessageContent(xmppMessage.getBody());
        Resource resource;

        if(xmppMessage.getType().equals(org.jivesoftware.smack.packet.Message.Type.groupchat)){
            resource =
                    new XmppResource(
                            XmppStringUtils.parseBareJid(xmppMessage.getFrom()),
                            XmppStringUtils.parseResource(xmppMessage.getFrom()),
                            Resource.Type.ROOM
                    );
        }else{
            resource = new XmppResource(xmppMessage.getFrom(),xmppMessage.getFrom(), Resource.Type.USER);
        }

        XmppRxMessage msg = new XmppRxMessage(resource,content);
        msg.setId(xmppMessage.getStanzaId());
        msg.setThread(xmppMessage.getThread());
        return msg;
    }
}
