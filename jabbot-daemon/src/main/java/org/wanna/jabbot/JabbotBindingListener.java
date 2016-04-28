package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.binding.event.BindingEvent;
import org.wanna.jabbot.binding.event.ConnectedEvent;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.privilege.PrivilegeGranter;
import org.wanna.jabbot.binding.privilege.PrivilegedAction;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;
import org.wanna.jabbot.handlers.EventHandler;
import org.wanna.jabbot.handlers.EventHandlerFactory;

import java.util.Queue;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public class JabbotBindingListener implements BindingListener{
	private final Queue<BindingEvent> queue;
    public JabbotBindingListener(Queue<BindingEvent> queue){
        this.queue = queue;
    }

	@Override
	public void eventReceived(BindingEvent event) {
		queue.offer(event);
	}
}
