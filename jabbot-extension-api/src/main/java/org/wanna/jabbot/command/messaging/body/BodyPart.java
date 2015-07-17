package org.wanna.jabbot.command.messaging.body;

/**
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-07-14
 */
public interface BodyPart {
    /**
     * Defines the available type of BodyPart
     */
    enum Type{
        /** Raw text body part **/
        TEXT,
        /** XHTML text body part **/
        XHTML
    }

    /**
     * Returns the text contained in the body part
     *
     * @return body part content
     */
    String getText();

    /**
     * Type of the body
     *
     * @see {@link org.wanna.jabbot.command.messaging.body.BodyPart.Type}
     * @return type
     */
    Type getType();
}
