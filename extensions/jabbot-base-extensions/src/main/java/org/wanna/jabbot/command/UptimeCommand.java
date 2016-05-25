package org.wanna.jabbot.command;

import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.messaging.DefaultMessageContent;
import org.wanna.jabbot.messaging.MessageContent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-09
 */
public class UptimeCommand extends AbstractCommandAdapter {

	public UptimeCommand(String commandName){
		super(commandName);
	}

	@Override
	public MessageContent process(CommandMessage message) {
		RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
		long uptime = rb.getUptime();

		final long secondsInMilli = 1000;
		final long minutesInMilli = secondsInMilli * 60;
		final long hoursInMilli = minutesInMilli * 60;
		final long daysInMilli = hoursInMilli * 24;

		long elapsedDays = uptime / daysInMilli;
		uptime = uptime % daysInMilli;

		long elapsedHours = uptime / hoursInMilli;
		uptime = uptime % hoursInMilli;

		long elapsedMinutes = uptime / minutesInMilli;
		uptime = uptime % minutesInMilli;

		long elapsedSeconds = uptime / secondsInMilli;
		String result = String.format("I'm up since %s day(s) %s hour(s) %s minute(s) and %s seconds",elapsedDays,elapsedHours,elapsedMinutes,elapsedSeconds);
		return new DefaultMessageContent(result);
	}
}
