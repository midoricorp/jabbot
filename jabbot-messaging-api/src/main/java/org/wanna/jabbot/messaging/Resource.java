package org.wanna.jabbot.messaging;

/**
 * A resource represent an entity which could either be a message sender or a messagage sender.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-01
 */
public interface Resource {
    enum Type{
        ROOM,
        USER
    }
    /**
     * Returns the address of a resource
     *
      * @return resource location
     */
    String getAddress();

    /**
     * Returns the resource name
     *
     * @return resource name
     */
    String getName();

    /**
     * Returns the type of resource
     * @return resource type
     */
    Type getType();
}
