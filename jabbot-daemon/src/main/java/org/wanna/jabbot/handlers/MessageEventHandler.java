package org.wanna.jabbot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.CommandManager;
import org.wanna.jabbot.DefaultCommandParser;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingMessage;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.privilege.PrivilegeGranter;
import org.wanna.jabbot.binding.privilege.PrivilegedAction;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class MessageEventHandler implements EventHandler<MessageEvent>{
	private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);

	@Override
	public void process(MessageEvent event) {
		final Binding binding = event.getBinding();
		final String commandPrefix = binding.getConfiguration().getCommandPrefix();
		final CommandParser commandParser = new DefaultCommandParser(commandPrefix);
		final Message message = event.getPayload();

		if(message == null || !message.getBody().startsWith(commandPrefix)){
			return;
		}

		if(message instanceof BindingMessage){
			BindingMessage bindingMessage = (BindingMessage)message;
			logger.debug("[JABBOT] received message on {}: {}",bindingMessage.getRoomName(),message.getBody());

			CommandParsingResult result = commandParser.parse(message.getBody());

			try {
				Command command = CommandManager.getInstanceFor(binding).getCommandFactory().create(result.getCommandName());
				if(command instanceof PrivilegedAction){
					if(binding instanceof PrivilegeGranter){
						boolean canExecute = ((PrivilegeGranter)binding).canExecute(message.getSender(),((BindingMessage) message).getDestination(),(PrivilegedAction)command);
						if(!canExecute){
							logger.debug("user {} cannot execute {}", message.getSender().getAddress(), command.getCommandName());
							return;
						}
					}
				}
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
