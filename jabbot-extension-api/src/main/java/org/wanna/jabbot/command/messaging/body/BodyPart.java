package org.wanna.jabbot.command.messaging.body;

/**
 * BodyPart is a class which is part of a Message.
 * A Message body can be composed of one or many BodyPart.
 * Each BodyPart representing a variant of the message.
 * For example a message could contains a raw text message as a BodyPart
 * but also present a second BodyPart which is the XHTML representation of the text
 * <p/>
 * A BodyPart is formed of a String representing the content
 * and a Type representing it's type (xhtml, text, ...)
 *
 * @see {@link org.wanna.jabbot.command.messaging.Message}
 *
 * @author Vincent Morsiani
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
