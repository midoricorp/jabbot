package org.wanna.jabbot.extensions.demothread;

import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.DefaultMessage;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.MessageSender;
import org.wanna.jabbot.command.messaging.MessageSenderAware;
import org.wanna.jabbot.command.config.CommandConfig;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-24
 */
public class DemoThreadCommand extends AbstractCommandAdapter implements MessageSenderAware{
	MessageSender messageSender;

	public DemoThreadCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public Message process(final Message message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					DefaultMessage result = new DefaultMessage();
					result.setBody("hello world");
					messageSender.sendMessage(result);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
		return null;
	}

	@Override
	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
}
