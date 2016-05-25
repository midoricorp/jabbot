package org.wanna.jabbot.messaging;

import org.wanna.jabbot.messaging.body.BodyPart;
import java.util.Collection;

/**
 * Represents a MessageContent which can be received or sent by the core.
 * Messages are dispatched from binding to commands on the incoming message flow
 * and are then returned by Command themselves and dispatched back to the binding.
 * <p/>
 * A MessageContent will always contains information about:
 * <ul>
 *     <li>The MessageContent sender</li>
 *     <li>The various BodyPart which compose the message</li>
 * </ul>
 *
 * @see {@link BodyPart}
 * @author vmorsiani <vmorsiani>
 * @since 2015-03-06
 */
public interface MessageContent {
    /**
     * Raw text representation of the message body.
     * Is equivalent to getBody("TEXT")
     *
     * @return message raw text
     */
    String getBody();

    /**
     * Retrieves the BodyPart of a given type from the MessageContent
     * If no matching BodyPart is found, a NULL value will be returned
     *
     * @param type type of BodyPart to retrieve
     * @return BodyPart corresponding to the type.
     */
	BodyPart getBody(BodyPart.Type type);

    /**
     * Retrieves ALL the BodyPart which compose a MessageContent
     *
     * @return Collection of BodyPart
     */
    Collection<BodyPart> getBodies();

    /**
     * Add a BodyPart to the message
     * @param body BodyPart to be added
     */
    void addBody(BodyPart body);
}
