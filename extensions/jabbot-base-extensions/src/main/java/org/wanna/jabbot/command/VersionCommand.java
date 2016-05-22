package org.wanna.jabbot.command;

import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.MessageContent;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-01
 */
public class VersionCommand extends AbstractCommandAdapter {

	public VersionCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public MessageContent process(CommandMessage message) {
		StringBuilder sb = new StringBuilder();
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		sb.append("I am running Jabot beta\n")
				.append("on a ")
				.append(os.getName())
				.append(" ")
				.append(os.getArch())
				.append(" ( ")
				.append(os.getVersion())
				.append(" )").append("\n")
				.append("https://github.com/vmorsiani/jabbot");

		return new DefaultMessageContent(sb.toString());
	}
}
