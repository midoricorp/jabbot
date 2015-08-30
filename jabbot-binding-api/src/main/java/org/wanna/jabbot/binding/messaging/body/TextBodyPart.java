package org.wanna.jabbot.binding.messaging.body;

/**
 * This is the Raw Text implementation of BodyPart
 *
 * @see {@link BodyPart}
 *
 * @author Vincent Morsiani
 * @since 2015-07-14
 */
public class TextBodyPart implements BodyPart {
    private final String text;

    /**
     * Constructor
     * @param text raw text
     */
    public TextBodyPart(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BodyPart.Type getType() {
        return Type.TEXT;
    }
}
