package org.wanna.jabbot.binding.matrix;

import com.sipstacks.xhml.Emojiify;
import com.sipstacks.xhml.XHTMLObject;
import com.sipstacks.xhml.XHtmlConvertException;
import de.jojii.matrixclientserver.Bot.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

class HtmlReformat {
    private String formattedMessage;
    private Client client;

    private final Logger logger = LoggerFactory.getLogger(HtmlReformat.class);

    public HtmlReformat(Client client, String formattedMessage) {
        this.client = client;
        this.formattedMessage = formattedMessage;
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

                        synchronized (client) {
                            logger.info("mapping " + url.getTextContent());
                            logger.info("content-type: " +  http.getContentType());
                            logger.info("content-length: " + http.getContentLength());
                            boolean finished = false;
                            client.sendFile(http.getContentType(),http.getContentLength(),http.getInputStream(), result -> {
                                try {
                                    String s = result.toString();
                                    logger.info("Result of upload: " + s);
                                    if (s.startsWith("mxc")) {
                                        url.setTextContent(s);
                                    }
                                } catch(Exception e) {
                                    logger.error("Got an exception on image upload", e);
                                }
                                client.notify();
                                finished = true;
                            });
                            try {
                                if (!finished) {
                                    client.wait();
                                }
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

    public String invoke() {
        XHTMLObject obj = new XHTMLObject();
        try {
            obj.parse(formattedMessage);
            updateImages(obj.objects);
            Emojiify.convert(obj);
            formattedMessage = obj.getString();

        } catch (IOException | XHtmlConvertException | TransformerException e) {
            logger.error("unable to parse xhtml", e);
        }
        return formattedMessage;
    }
}
