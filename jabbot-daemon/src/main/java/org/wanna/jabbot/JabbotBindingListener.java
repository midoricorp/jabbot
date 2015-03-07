package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandMessage;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public class JabbotBindingListener implements BindingListener{
	final Logger logger = LoggerFactory.getLogger(JabbotBindingListener.class);
	private final CommandParser commandParser;
	private final String commandPrefix;

	public JabbotBindingListener(String commandPrefix) {
		this.commandPrefix = commandPrefix;
		commandParser = new DefaultCommandParser(commandPrefix);
	}

	@Override
	public void onMessage(Binding binding, BindingMessage message) {
		if(message == null || !message.getBody().startsWith(commandPrefix)){
			return;
		}

		logger.debug("[JABBOT] received message on {}: {}",message.getRoomName(),message.getBody());
		CommandParsingResult result = commandParser.parse(message.getBody());

		try {
			Command command = binding.getCommandFactory().create(result.getCommandName());
			DefaultCommandMessage wrapper = new DefaultCommandMessage();
			wrapper.setBody(result.getRawArgsLine());
			wrapper.setSender(message.getSender());
			wrapper.setRoomName(message.getRoomName());
			CommandMessage commandResult = command.process(wrapper);
			if(commandResult == null){
				logger.warn("Aborting due to undefined command result for command {}",command.getClass());
				return;
			}
			BindingMessage response = new BindingMessage(message.getRoomName(),message.getSender(),commandResult.getBody());
			binding.sendMessage(response);
		} catch (CommandNotFoundException e) {
			logger.debug("command not found: '{}'", e.getCommandName());
		}
	}
}
