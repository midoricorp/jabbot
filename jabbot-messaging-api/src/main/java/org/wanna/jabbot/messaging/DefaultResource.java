package org.wanna.jabbot.messaging;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-02
 */
public class DefaultResource implements Resource{
    private final String address;
    private final String name;
    private final Type type;

    public DefaultResource(String address, String name) {
        this.address = address;
        this.name = name;
        this.type = Type.ROOM;
    }

    public DefaultResource(String address, String name, Type type) {
        this.address = address;
        this.name = name;
        this.type = type;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }
}
