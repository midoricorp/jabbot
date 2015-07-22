package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.body.BodyPart;

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
     * It will check for the presence of XHTML Body, and feed the XMPP with it if present.
     * Otherwise, only the raw TEXT message will be fed.
     *
     * @param message Jabbot Message
     * @return Xmpp Message
     */
    public static org.jivesoftware.smack.packet.Message createXmppMessage(Message message){
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
            //TODO validate XHTML here and throw an exception if message doesn't appear to be syntaxically valid.
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
        return xmppMessage;
    }
}
