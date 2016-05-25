package org.wanna.jabbot.command.messaging;

import org.wanna.jabbot.messaging.Resource;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-21
 */
public class DefaultCommandMessage implements CommandMessage {
	private Resource sender;
    private String argsLine;

    public DefaultCommandMessage(Resource sender, String argsLine) {
        this.sender = sender;
        this.argsLine = argsLine;
    }

    @Override
    public Resource getSender() {
        return sender;
    }

    @Override
    public String getArgsLine() {
        return argsLine;
    }
}
