package org.wanna.jabbot.binding.messaging.body;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Validator class for Xhtml fragment.
 *
 * @author Vincent Morsiani
 * @since 2015-07-23
 */
public class XhtmlFragmentValidator implements BodyPartValidator<XhtmlBodyPart>{
    /**
     * Wrap the BodyPart text into an html & body tags and run a DOM parser against the produced XML.
     * If any error is encountered while parsing the Document, an {@link InvalidBodyPartException}
     * will be thrown.
     *
     * If the BodyPart appears to be NULL, then an InvalidBodyPartException will also be thrown.
     *
     * @param bodyPart the BodyPart to validate
     * @throws InvalidBodyPartException if any issue is encountered while parsing the XML Document.
     */
    @Override
    public void validate(XhtmlBodyPart bodyPart) throws InvalidBodyPartException {
        if(bodyPart == null ){
            throw new InvalidBodyPartException("BodyPart cannot be null");
        }
        try{
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final String wrappedMessage = "<html><body>"+bodyPart.getText()+"</body></html>";
            parser.parse(new InputSource(new StringReader(wrappedMessage)));
        }catch (ParserConfigurationException | SAXException | IOException e  ) {
            throw new InvalidBodyPartException(bodyPart,e);
        }
    }
}
