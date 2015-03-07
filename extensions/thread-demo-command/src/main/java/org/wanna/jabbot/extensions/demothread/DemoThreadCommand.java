package org.wanna.jabbot.extensions.demothread;

import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.CommandMessage;
import org.wanna.jabbot.command.DefaultCommandMessage;
import org.wanna.jabbot.command.MessageSender;
import org.wanna.jabbot.command.behavior.MessageSenderAware;
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
	public CommandMessage process(final CommandMessage message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					DefaultCommandMessage result = new DefaultCommandMessage();
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
