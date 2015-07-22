package org.wanna.jabbot.command.messaging.body;

/**
 * This is the XHTML implementation of BodyPart
 *
 * @see {@link org.wanna.jabbot.command.messaging.body.BodyPart}
 *
 * @author Vincent Morsiani
 * @since 2015-07-14
 */
public class XhtmlBodyPart implements BodyPart {
    private String xhtml;

    /**
     * Constructor
     * @param xhtml xhtml text
     */
    public XhtmlBodyPart(String xhtml) {
        this.xhtml = xhtml;
    }

    @Override
    public String getText() {
        return xhtml;
    }

    @Override
    public BodyPart.Type getType() {
        return BodyPart.Type.XHTML;
    }
}
