package org.wanna.jabbot.command;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.Chatroom;

import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class MucCommandListener implements PacketListener{
	final Logger logger = LoggerFactory.getLogger(MucCommandListener.class);
	CommandParser commandParser;
	CommandFactory commandFactory;

	private Map<String,Chatroom> chatrooms;

	public MucCommandListener(Map<String, Chatroom> chatrooms) {
		this.chatrooms = chatrooms;
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
			Chatroom room = getChatroom(message.getFrom());
			String resource = StringUtils.parseResource(message.getFrom());
			if(resource != null) {
				if (resource.equals(room.getNickname())) {
					logger.debug("not going to process my own stuff");
				} else {
					logger.debug("received a message from {} using chatroom {} and preparing response..", resource, room.getRoomName());
					ParsedCommand parsedCommand = commandParser.parse(message.getBody());

					logger.debug("parsedCommand : {}", parsedCommand.getCommandName());
					Command command = null;

					try {
						command = commandFactory.create(parsedCommand);
						command.process(room, message);

					} catch (CommandNotFoundException e) {
						logger.debug("command not found: '{}'", e.getCommandName());
					} catch (XMPPException e) {
						logger.error("error sending message", e);
					}

				}
			}
		}
	}

	private Chatroom getChatroom(final String from){
		String roomName = StringUtils.parseBareAddress(from);
		return chatrooms.get(roomName);
	}
}
