package org.wanna.jabbot;

import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.event.BindingEvent;

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
