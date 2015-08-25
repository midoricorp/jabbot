package org.wanna.jabbot.command.messaging;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-19
 */
public interface CommandMessage extends Message{
    void setBody(String body);
}
