package org.wanna.jabbot.command;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class CommandManager {

	private CommandParser commandParser;
	private CommandFactory commandFactory;
	private PacketListener listener;
	private PacketFilter filter;

	public CommandManager(CommandParser commandParser, CommandFactory commandFactory, PacketListener listener, PacketFilter filter) {
		this.commandParser = commandParser;
		this.commandFactory = commandFactory;
		this.listener = listener;
		this.filter = filter;
	}

	public CommandParser getCommandParser() {
		return commandParser;
	}

	public CommandFactory getCommandFactory() {
		return commandFactory;
	}

	public PacketListener getListener() {
		return listener;
	}

	public PacketFilter getFilter() {
		return filter;
	}
}
