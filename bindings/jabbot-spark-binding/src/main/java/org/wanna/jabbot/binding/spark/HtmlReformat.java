package org.wanna.jabbot.binding.spark;

import com.ciscospark.Person;
import com.sipstacks.xhml.Emojiify;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;

class HtmlReformat {
    private String formattedMessage;
    private Person me;

    private final Logger logger = LoggerFactory.getLogger(HtmlReformat.class);

    public HtmlReformat(Person me, String formattedMessage) {
        this.me = me;
        this.formattedMessage = formattedMessage;
        this.formattedMessage = this.formattedMessage.replace("<br>","<br/>");
        this.formattedMessage = this.formattedMessage.replaceAll("<img([^/>]*)>","<img$1/>");
    }

    private void removeMention(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) {
                if (item.getNodeName().equalsIgnoreCase("spark-mention")) {

                    Node objectId = item.getAttributes().getNamedItem("data-object-id");
                    if (objectId != null) {
                        String id = objectId.getTextContent();
                        if(id.equals(me.getId())) {
                            logger.info("Self mention found, removing!");
                            item.getParentNode().removeChild(item);
                            i--;
                        }
                    }

                }
                NodeList childNodes = item.getChildNodes();
                if(childNodes != null) removeMention(childNodes);
            }
        }
    }
    public String removeMentions() {
        XHTMLObject obj = new XHTMLObject();
        try {
            obj.parse(formattedMessage);
            if (me != null) {
                removeMention(obj.objects);
            }
            formattedMessage = obj.toText().trim();

        } catch (XHtmlConvertException e) {
            logger.error("unable to parse xhtml", e);
        }
        return formattedMessage;
    }

    public String emojiify() {
        XHTMLObject obj = new XHTMLObject();
        try {
            obj.parse(formattedMessage);

            Emojiify.convert(obj);

            formattedMessage = obj.getString().trim();

        } catch (XHtmlConvertException | TransformerException e) {
            logger.error("unable to parse xhtml", e);
        }
        return formattedMessage;
    }
}
