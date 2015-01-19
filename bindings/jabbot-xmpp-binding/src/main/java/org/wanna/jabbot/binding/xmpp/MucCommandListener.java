package org.wanna.jabbot.binding.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.ParsedCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class MucCommandListener implements PacketListener{
	final Logger logger = LoggerFactory.getLogger(MucCommandListener.class);
	private CommandParser commandParser;
	private CommandFactory commandFactory;
	private Map<String,Room> rooms;

	public MucCommandListener() {
		rooms = new HashMap<>();
	}

	public MucCommandListener(Map<String,Room> rooms){
		this.rooms = rooms;
	}

	public void setRooms(Map<String, Room> rooms) {
		this.rooms = rooms;
	}

	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}

	@Override
	public void processPacket(Packet packet) throws SmackException.NotConnectedException {

		if(packet instanceof Message){
			Message message = (Message)packet;
			logger.debug("received packet from {} with body: {}",message.getFrom(),message.getBody());
			Room room = getChatroom(message.getFrom());
			logger.trace("loaded room {}", room.getConfiguration().getName());
			String resource = StringUtils.parseResource(message.getFrom());
			if(resource != null) {
				if (resource.equals(room.getConfiguration().getNickname())) {
					logger.debug("not going to process my own stuff");
				} else {
					logger.debug("received a message from {} using chatroom {} and preparing response..", resource, room.getConfiguration().getName());
					ParsedCommand parsedCommand = commandParser.parse(message.getBody());

					logger.debug("parsedCommand : {}", parsedCommand.getCommandName());
					Command command = null;

					try {
						command = commandFactory.create(parsedCommand);
						MessageWrapper wrapper = new MessageWrapper(message);
						wrapper.setSender(StringUtils.parseResource(message.getFrom()));
						command.process(room, wrapper);
					} catch (CommandNotFoundException e) {
						logger.debug("command not found: '{}'", e.getCommandName());
					}
				}
			}
		}
	}

	private Room getChatroom(final String from){
		final String roomName = StringUtils.parseBareAddress(from);
		logger.trace("loading room with name {}",roomName);
		Room room = rooms.get(roomName);
		return room;
	}
}
