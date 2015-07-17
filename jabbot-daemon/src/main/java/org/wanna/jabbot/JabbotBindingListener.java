package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;
import org.wanna.jabbot.command.messaging.body.BodyPart;
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
	public void onMessage(Binding binding, Message message) {
		if(message == null || !message.getBody().startsWith(commandPrefix)){
			return;
		}

        logger.debug("[JABBOT] received message on {}: {}",message.getRoomName(),message.getBody());

		CommandParsingResult result = commandParser.parse(message.getBody());

		try {
			Command command = binding.getCommandFactory().create(result.getCommandName());
			DefaultMessage wrapper = new DefaultMessage(result.getRawArgsLine());
			wrapper.setSender(message.getSender());
			wrapper.setRoomName(message.getRoomName());
			Message commandResult = command.process(wrapper);
			if(commandResult == null){
				logger.warn("Aborting due to undefined command result for command {}",command.getClass());
				return;
			}

			Message response = createResponseFromResult(commandResult,message.getSender(),message.getRoomName());
            binding.sendMessage(response);
		} catch (CommandNotFoundException e) {
			logger.debug("command not found: '{}'", e.getCommandName());
		}
	}

    /**
     * Create a response message out of the command result
     *
     * @param result command result
     * @param sender sender of the message
     * @param roomName room name from which the message has been sent
     * @return response message
     */
    private Message createResponseFromResult(Message result, String sender, String roomName){
        Message response = new DefaultMessage(result.getBody(),sender,roomName);
        for (BodyPart body : result.getBodies()) {
            response.addBody(body);
        }
        return response;
    }
}
