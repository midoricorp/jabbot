package org.wanna.jabbot.binding.matrix;

import com.sipstacks.xhml.Emojiify;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;
import de.jojii.matrixclientserver.Bot.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HtmlReformat {
    private String formattedMessage;
    private Client client;
    XHTMLObject obj = new XHTMLObject();

    private static final Logger logger = LoggerFactory.getLogger(HtmlReformat.class);

    public HtmlReformat(Client client, String formattedMessage) {
        this.client = client;
        this.formattedMessage = formattedMessage;
    }

    private static String getStyleAttribute(String style, String attrib) {
        String [] attribs = style.split(";");
        for(String attr : attribs) {
            System.err.println("Got attrib: " + attr);
            Pattern pattern = Pattern.compile(attrib + ":\\s+([#0-9a-zA-Z]+)");
            Matcher matcher = pattern.matcher(attr);
            if (!matcher.matches()) {
                continue;
            }
            return matcher.group(1);
        }
        return null;
    }

    private void updateTags(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) {
                if (item.getNodeName().equalsIgnoreCase("span")) {

                    Node styleNode = item.getAttributes().getNamedItem("style");
                    if (styleNode != null) {
                        String style = styleNode.getTextContent();
                        String color = getStyleAttribute(style, "color");
                        String bgcolor = getStyleAttribute(style, "background-color");
                        if (color != null) {
                            ((Element) item).setAttribute("data-mx-color", color);
                        }
                        if (bgcolor != null) {
                            ((Element) item).setAttribute("data-mx-bg-color", color);
                        }
                    }

                } else if (item.getNodeName().equalsIgnoreCase("font")) {
                    Node color = item.getAttributes().getNamedItem("color");
                    if(color != null) {
                        ((Element) item).setAttribute("data-mx-color", color.getTextContent());
                    }

                }
                NodeList childNodes = item.getChildNodes();
                if(childNodes != null) updateTags(childNodes);
            }
        }
    }

    private void updateImages(NodeList nodeList) throws IOException {
        for(int i = 0; i < nodeList.getLength(); i++ ) {
            Node item = nodeList.item(i);
            if(item.getNodeType() != Node.TEXT_NODE) {
                if(item.getNodeName().equalsIgnoreCase("img")){
                    Node url = item.getAttributes().getNamedItem("src");
                    if (url != null) {
                        logger.info("found an img tag of " + url.getTextContent());
                        URL obj = new URL(url.getTextContent());
                        URLConnection con = obj.openConnection();
                        HttpURLConnection http = (HttpURLConnection) con;
                        http.setRequestMethod("GET");
                        http.connect();
			final Object syncSrc = new Object();

                        synchronized (syncSrc) {
                            logger.info("mapping " + url.getTextContent());
                            logger.info("content-type: " +  http.getContentType());
                            logger.info("content-length: " + http.getContentLength());
                            client.sendFile(http.getContentType(),http.getContentLength(),http.getInputStream(), result -> {
                                try {
                                    String s = result.toString();
                                    logger.info("Result of upload: " + s);
                                    if (s.startsWith("mxc")) {
                                        url.setTextContent(s);
                                    	logger.info("Node updated");
                                    }
                                } catch(Exception e) {
                                    logger.error("Got an exception on image upload", e);
                                }
				synchronized(syncSrc) {
                                	syncSrc.notify();
				}
				logger.info("caller notified");
                            });
                            try {
                                 syncSrc.wait();
                            } catch (InterruptedException e) {
                                logger.error("Interruped on updating url", e);
                            }

                        }
                    }
                }
                NodeList childNodes = item.getChildNodes();
                if(childNodes != null) updateImages(childNodes);
            }
        }

    }
    private String findRemoveImage(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.TEXT_NODE) {
                if (item.getNodeName().equalsIgnoreCase("img")) {

                    Node objectId = item.getAttributes().getNamedItem("src");
                    if (objectId != null) {
                        item.getParentNode().removeChild(item);
                        return objectId.getTextContent();
                    }
                }
                NodeList childNodes = item.getChildNodes();
                if (childNodes != null) {
                    String result = findRemoveImage(childNodes);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }
    public String findAndRemoveImage() {
        return findRemoveImage(obj.objects);
    }
    public String getString() {
        String newMsg = null;
        try {
            newMsg = obj.getString();
        } catch(TransformerException e) {
            logger.error("unable to generate xhtml", e);
        }
        return newMsg;
    }

    public void invoke() {
        try {

            obj.parse(formattedMessage);
            updateImages(obj.objects);
            updateTags(obj.objects);
            Emojiify.convert(obj);

        } catch (IOException | XHtmlConvertException | TransformerException e) {
            logger.error("unable to parse xhtml", e);
        }
    }
}
