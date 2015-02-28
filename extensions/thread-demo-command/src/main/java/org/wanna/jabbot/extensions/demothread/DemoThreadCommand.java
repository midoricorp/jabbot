package org.wanna.jabbot.extensions.demothread;

import org.wanna.jabbot.command.CommandResult;
import org.wanna.jabbot.command.MessageSender;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.behavior.MessageSenderAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;

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
	public CommandResult process(final MessageWrapper message) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					CommandResult result = new CommandResult();
					result.setText("hello world");
					messageSender.sendMessage(message, result);
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
