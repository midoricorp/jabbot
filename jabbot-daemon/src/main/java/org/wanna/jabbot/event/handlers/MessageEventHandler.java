package org.wanna.jabbot.event.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.BindingContainer;
import org.wanna.jabbot.DefaultCommandParser;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.event.OutgoingMessageEvent;
import org.wanna.jabbot.messaging.DefaultTxMessage;
import org.wanna.jabbot.messaging.MessageContent;
import org.wanna.jabbot.messaging.RxMessage;
import org.wanna.jabbot.messaging.TxMessage;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;
import org.wanna.jabbot.event.EventDispatcher;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class MessageEventHandler implements EventHandler<MessageEvent>{
	private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);

	@Override
	public boolean process(MessageEvent event, EventDispatcher dispatcher) {
		final Binding binding = event.getBinding();
		final String commandPrefix = binding.getConfiguration().getCommandPrefix();
		final CommandParser commandParser = new DefaultCommandParser(commandPrefix);
		final RxMessage request = event.getPayload();
		final MessageContent messageContent = request.getMessageContent();

		//Discard if request is null or do not start with the proper prefix
		if(!messageContent.getBody().startsWith(commandPrefix)){
			return false;
		}

		logger.debug("[JABBOT] received request from {}: {}",request.getSender(), messageContent.getBody());
		CommandParsingResult result = commandParser.parse(messageContent.getBody());

		try {
			Command command = BindingContainer.getInstance(binding.getIdentifier()).getCommandFactory().create(result.getCommandName());
			DefaultCommandMessage commandMessage = new DefaultCommandMessage(request.getSender(),result.getRawArgsLine());
			MessageContent commandResult = command.process(commandMessage);
			if(commandResult == null){
				logger.warn("Aborting due to undefined command result for command {}",command.getClass());
				return false;
			}

			if(!commandResult.getBodies().isEmpty()){
				TxMessage response = new DefaultTxMessage(commandResult,request.getSender(),request);
				dispatcher.dispatch(new OutgoingMessageEvent(binding,response));
				return true;
			}
		} catch (CommandNotFoundException e) {
			logger.debug("command not found: '{}'", e.getCommandName());
			return false;
		}
		return true;
	}
}
