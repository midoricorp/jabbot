package org.wanna.jabbot.command.messaging.body;

/**
 * Validator for the BodyPart.
 * It will check validity of a BodyPart and throw an {@link InvalidBodyPartException}
 * If the message is declared invalid
 *
 * @author Vincent Morsiani
 * @since 2015-07-23
 */
public interface BodyPartValidator<T extends BodyPart> {
    /**
     * Performs a validation on a BodyPart and throws an {@link org.wanna.jabbot.command.messaging.body.InvalidBodyPartException}
     * if any validation exception is raised
     *
     * @param bodyPart the BodyPart to validate
     * @throws InvalidBodyPartException Exception raised if BodyPart is not valid
     */
    void validate(final T bodyPart) throws InvalidBodyPartException;
}
