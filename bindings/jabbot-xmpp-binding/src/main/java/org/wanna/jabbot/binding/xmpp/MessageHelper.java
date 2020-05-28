package org.wanna.jabbot.binding.xmpp;

import com.sipstacks.xhml.Emojiify;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;
import emoji4j.EmojiManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.jxmpp.util.XmppStringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wanna.jabbot.messaging.*;
import org.wanna.jabbot.messaging.body.BodyPart;

import javax.xml.transform.TransformerException;

/**
 * MessageHelper is a class which provide some facilities with regards to Xmpp message.
 *
 * @author Vincent Morsiani
 * @since 2015-07-22
 */
public final class MessageHelper {

    static {
        EmojiManager.addStopWords(":[0-9]+");
    }
    
    /**
     * List of characters we don't want to see in an XMPP message body
     */
    private final static char[] escapeChars = new char[]{'\f','\b'};
    static Logger logger = LoggerFactory.getLogger(MessageHelper.class);

    /**
     * Do not allow instance creation of MessageHelper
     */
    private MessageHelper(){

    }
    private static void updateTags(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) {
                if (item.getNodeName().equalsIgnoreCase("font")) {
                    /* xmpp doesn't understand font, so covert it to span + css */
                    item.getOwnerDocument().renameNode(item,null,"span");
                    StringBuffer styleString = new StringBuffer();

                    Node colorNode = item.getAttributes().getNamedItem("color");
                    if (colorNode != null) {
                        String color = colorNode.getTextContent();

                        if (color != null) {
                            styleString.append("color: ").append(color).append(";");

                        }
                    }
                    Node bgColorNode = item.getAttributes().getNamedItem("bgcolor");
                    if (bgColorNode != null) {
                        String bgcolor = bgColorNode.getTextContent();

                        if (bgcolor != null) {
                            styleString.append("background-color: ").append(bgcolor).append(";");

                        }
                    }

                    String style = styleString.toString();
                    if(style.length() > 0) {
                        ((Element) item).setAttribute("style", style);
                    }

                }
                NodeList childNodes = item.getChildNodes();
                if(childNodes != null) updateTags(childNodes);
            }
        }
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
            XHTMLObject obj = new XHTMLObject();
            String xhtmlPartText = xhtmlPart.getText();
            try {
                obj.parse(xhtmlPartText);
                updateTags(obj.objects);
                Emojiify.convert(obj);
                xhtmlPartText = obj.getString();
                logger.info("After re-formatting html\n" + xhtmlPartText);
            } catch (XHtmlConvertException | TransformerException e) {
                logger.error("unable to parse xhtml\n" + xhtmlPartText, e);
            }
            XmlStringBuilder sb = new XmlStringBuilder();
            sb.append("<body>");
            sb.append(xhtmlPartText);
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
                    new DefaultResource(
                            XmppStringUtils.parseBareJid(xmppMessage.getFrom()),
                            XmppStringUtils.parseResource(xmppMessage.getFrom()),
                            Resource.Type.ROOM
                    );
        }else{
            resource = new DefaultResource(xmppMessage.getFrom(),xmppMessage.getFrom(), Resource.Type.USER);
        }

        XmppRxMessage msg = new XmppRxMessage(resource,content);
        msg.setId(xmppMessage.getStanzaId());
        msg.setThread(xmppMessage.getThread());
        return msg;
    }
}
