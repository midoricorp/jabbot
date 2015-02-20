package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.domain.messages.ChannelPrivMsg;
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;

import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-15
 */
public class RoomListener extends VariousMessageListenerAdapter{
	final Logger logger = LoggerFactory.getLogger(RoomListener.class);
	private CommandFactory commandFactory;
	private CommandParser commandParser;
	private Map<String,Room> rooms;

	public RoomListener(CommandFactory commandFactory, CommandParser commandParser) {
		this.commandFactory = commandFactory;
		this.commandParser = commandParser;
	}

	@Override
	public void onChannelMessage(ChannelPrivMsg aMsg) {

		logger.debug("received {} on {}",aMsg.getText(),aMsg.getChannelName());
		if(aMsg != null && aMsg.getText().startsWith(commandParser.getCommandPrefix())){
			CommandParsingResult result = commandParser.parse(aMsg.getText());
			try {

				Command command = commandFactory.create(result.getCommandName());
				List<String> args = command.getArgsParser().parse(result.getRawArgsLine());
				MessageWrapper wrapper = new MessageWrapper(aMsg);
				wrapper.setArgs(args);
				wrapper.setSender(aMsg.getSource().getNick());
				command.process(getRoom(aMsg.getChannelName()),wrapper);
			} catch (CommandNotFoundException e) {
				logger.error("erorr instantating command",e);
			}
		}
		super.onChannelMessage(aMsg);
	}

	private Room getRoom(String roomName){
		return rooms.get(roomName);
	}

	public void setRooms(Map<String, Room> rooms) {
		this.rooms = rooms;
	}
}
