package org.wanna.jabbot.command.messaging.body;

/**
 * Exception which is raised during the validation phase of a BodyPart
 *
 * @author Vincent Morsiani
 * @since 2015-07-23
 */
public class InvalidBodyPartException extends Exception{
    private BodyPart invalidBodyPart;

    /**
     * Consutrctor of the InvalidBodyPartException
     *
     * @param message error message
     */
    public InvalidBodyPartException(String message) {
        super(message);
    }

    /**
     * Constructor of the InvalidBodyPartException
     *
     * @param bodyPart the invalid BodyPart which raised the exception
     * @param throwable The root cause of the validation failure.
     */
    public InvalidBodyPartException(BodyPart bodyPart, Throwable throwable){
        super("Invalid BodyPart",throwable);
        this.invalidBodyPart = bodyPart;
    }

    /**
     * Retrieve the invalid BodyPart
     *
     * @return BodyPart which has been declared invalid.
     */
    public BodyPart getInvalidBodyPart() {
        return invalidBodyPart;
    }
}
