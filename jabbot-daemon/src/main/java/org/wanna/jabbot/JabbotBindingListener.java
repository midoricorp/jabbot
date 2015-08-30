package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingListener;
import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
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
    private final Binding binding;

    public JabbotBindingListener(final Binding binding, final String commandPrefix){
        this.commandPrefix = commandPrefix;
        this.binding = binding;
        commandParser = new DefaultCommandParser(commandPrefix);
    }

	@Override
	public void onMessage(Message message) {
		if(message == null || !message.getBody().startsWith(commandPrefix)){
			return;
		}

        if(message instanceof BindingMessage){
            BindingMessage bindingMessage = (BindingMessage)message;
            logger.debug("[JABBOT] received message on {}: {}",bindingMessage.getRoomName(),message.getBody());

            CommandParsingResult result = commandParser.parse(message.getBody());

            try {
                Command command = CommandManager.getInstanceFor(binding).getCommandFactory().create(result.getCommandName());
                DefaultCommandMessage commandMessage = new DefaultCommandMessage(result.getRawArgsLine());
                commandMessage.setSender(message.getSender());
                Message commandResult = command.process(commandMessage);
                if(commandResult == null){
                    logger.warn("Aborting due to undefined command result for command {}",command.getClass());
                    return;
                }

                Message response = binding.createResponseMessage(bindingMessage, commandResult);
                //Only send message if it has at least 1 body part
                if(response != null && !response.getBodies().isEmpty()){
                    binding.sendMessage((BindingMessage)response);
                }
            } catch (CommandNotFoundException e) {
                logger.debug("command not found: '{}'", e.getCommandName());
            }

        }
	}
}
