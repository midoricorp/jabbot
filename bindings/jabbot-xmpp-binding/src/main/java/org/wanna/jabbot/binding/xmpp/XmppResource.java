package org.wanna.jabbot.binding.xmpp;

import org.wanna.jabbot.binding.messaging.Resource;

/**
 * XMPP specific implementation of Resource.
 * It is used as a placeholder to store information about message receiver & senders
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-01
 */
public class XmppResource implements Resource{
    private final String address;
    private final String name;
    private final Type type;

    /**
     * Constructor which takes the address and name alias as argument.
     * It will assume that the Resource type is a ROOM
     *
     * @param address address of the resource
     * @param name name alias for the resource
     */
    public XmppResource(final String address, final String name){
        this.address = address;
        this.name = name;
        type = Type.ROOM;
    }

    public XmppResource(String address, String name, Type type) {
        this.address = address;
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        return type;
    }
}
