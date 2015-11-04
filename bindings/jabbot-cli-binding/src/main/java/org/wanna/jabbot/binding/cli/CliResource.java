package org.wanna.jabbot.binding.cli;

import org.wanna.jabbot.binding.messaging.Resource;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-01
 */
public class CliResource implements Resource {
    private final String address;
    private final String name;

    public CliResource(final String address, final String name){
        this.address = address;
        this.name = name;
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
        return Type.ROOM;
    }
}
