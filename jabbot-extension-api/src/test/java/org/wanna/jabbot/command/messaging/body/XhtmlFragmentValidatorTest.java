package org.wanna.jabbot.command.messaging.body;

import org.junit.Assert;
import org.junit.Test;
import org.wanna.jabbot.messaging.body.BodyPartValidator;
import org.wanna.jabbot.messaging.body.InvalidBodyPartException;
import org.wanna.jabbot.messaging.body.XhtmlBodyPart;
import org.wanna.jabbot.messaging.body.XhtmlFragmentValidator;
import org.xml.sax.SAXParseException;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author Vincent Morsiani
 * @since 2015-07-23
 */
public class XhtmlFragmentValidatorTest {
    BodyPartValidator<XhtmlBodyPart> validator = new XhtmlFragmentValidator();


    @Test
    public void createValidXhtml() throws InvalidBodyPartException {
        final String xhtml = "<a href =\"http://www.test.com\">test</a>";
        validator.validate(new XhtmlBodyPart(xhtml));
    }
    @Test
    public void plainTextBodyPart() throws InvalidBodyPartException{
        final String xhtml = "this should be valid text";
        validator.validate(new XhtmlBodyPart(xhtml));
    }

    @Test(expected =InvalidBodyPartException.class )
    public void createUnbalancedTag() throws InvalidBodyPartException{
        final String xhtml = "<a href =\"http://www.test.com\">test";
        validator.validate(new XhtmlBodyPart(xhtml));
    }

    @Test(expected = InvalidBodyPartException.class)
    public void closeBodyIsInvalid() throws InvalidBodyPartException{
        final String xhtml = "</body>";
        validator.validate(new XhtmlBodyPart(xhtml));
    }

    @Test
    public void doubleBodyIsValid() throws InvalidBodyPartException{
        final String xhtml = "<body><a href =\"http://www.test.com\">test</a></body>";
        validator.validate(new XhtmlBodyPart(xhtml));
    }

    @Test(expected = InvalidBodyPartException.class)
    public void nullBodyPartIsInvalid() throws InvalidBodyPartException{
        validator.validate(null);
    }

    @Test
    public void nullTextIsInvalid() throws InvalidBodyPartException{
        validator.validate(new XhtmlBodyPart(null));
    }

    @Test
    public void validateExceptionContent(){
        final String xhtml = "</body>";
        XhtmlBodyPart bodyPart = new XhtmlBodyPart(xhtml);
        try {
            validator.validate(bodyPart);
            Assert.fail("Should have thrown an InvalidBodyPartException");
        }catch (InvalidBodyPartException e){
            Assert.assertThat(e.getInvalidBodyPart(),not(nullValue()));
            Assert.assertThat(e.getInvalidBodyPart().getType(),is(bodyPart.getType()));
            Assert.assertThat(e.getInvalidBodyPart().getText(),is(bodyPart.getText()));
            Assert.assertThat(e.getCause(),instanceOf(SAXParseException.class));
        }
    }
}
