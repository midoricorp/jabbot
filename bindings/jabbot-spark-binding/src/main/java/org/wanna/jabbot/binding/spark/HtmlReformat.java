package org.wanna.jabbot.binding.spark;

import com.ciscospark.Person;
import com.sipstacks.xhml.Emojiify;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;

import emoji4j.EmojiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;

class HtmlReformat {
    private String formattedMessage;
    private Person me;

    private static final Logger logger = LoggerFactory.getLogger(HtmlReformat.class);

    static {
        EmojiManager.addStopWords(":[0-9]+");
    }

    public HtmlReformat(Person me, String formattedMessage) {
        this.me = me;
        this.formattedMessage = formattedMessage;
        this.formattedMessage = this.formattedMessage.replace("<br>","<br/>");
        this.formattedMessage = this.formattedMessage.replaceAll("<img([^>]*[^/])>","<img$1/>");
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

    public String unescape() {
        boolean inQuote = false;
        boolean inCode = false;
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < formattedMessage.length();i++) {
            char c = formattedMessage.charAt(i);

            if (c =='"') {
                inQuote = !inQuote;
            }

            if (c == '`') {
                if (i+2 < formattedMessage.length()) {
                    if (formattedMessage.charAt(i+1) == '`'
                            && formattedMessage.charAt(i+2) == '`') {
                        inCode = !inCode;
                        i+=2;
                        continue;
                    }
                }
            }

            if (inCode) {
                inQuote = false;
                sb.append(c);
                continue;
            }
            
            if (c == '\\')  {
                if (inQuote) {
                    if (i+1 < formattedMessage.length()) {
                        char next = formattedMessage.charAt(i+1);
                        switch (next) {
                            case 'n':
                                sb.append("\\");
                                break;
                            default:
                                sb.append("\\\\");
                        }
                    } else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        formattedMessage = sb.toString();
        return formattedMessage;
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

    private static String findImage(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) {
                if (item.getNodeName().equalsIgnoreCase("img")) {

                    Node objectId = item.getAttributes().getNamedItem("src");
                    if (objectId != null) {
                        return objectId.getTextContent();
                    }
                }
                NodeList childNodes = item.getChildNodes();
                if (childNodes != null) {
                    String result = findImage(childNodes);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }
    public static String findImage(String msg) {
        XHTMLObject obj = new XHTMLObject();
        try {
            obj.parse(msg);
            return findImage(obj.objects);
        } catch (XHtmlConvertException e) {
            logger.error("Unable to parse xhtml", e);
        }
        return null;
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
