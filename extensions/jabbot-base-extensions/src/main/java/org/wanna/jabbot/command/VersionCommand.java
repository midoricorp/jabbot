package org.wanna.jabbot.command;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-01
 */
public class VersionCommand extends AbstractCommand{
	@Override
	public String getCommandName() {
		return "version";
	}

	@Override
	public void process(MucHolder chatroom, Message message) throws XMPPException, SmackException.NotConnectedException {
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

		chatroom.getMuc().sendMessage(sb.toString());
	}
}
