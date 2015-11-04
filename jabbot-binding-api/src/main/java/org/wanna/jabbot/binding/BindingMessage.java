package org.wanna.jabbot.binding;


import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.messaging.Resource;

/**
 * Interface which defines messages which flows between jabbot core and the bindings
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-20
 */
public interface BindingMessage extends Message {
    /**
     * Retrieve the resource to which the Message has been sent
     *
     * @return destination resource
     */
    Resource getDestination();

    /**
     * Retrieves the room name from which the message has been sent
     * @return room name
     */
    String getRoomName();
}
