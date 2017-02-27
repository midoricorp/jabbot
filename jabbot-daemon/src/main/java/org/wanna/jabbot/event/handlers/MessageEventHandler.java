package org.wanna.jabbot.event.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.CommandManager;
import org.wanna.jabbot.DefaultCommandParser;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.event.MessageEvent;
import org.wanna.jabbot.binding.event.OutgoingMessageEvent;
import org.wanna.jabbot.messaging.DefaultResource;
import org.wanna.jabbot.messaging.DefaultTxMessage;
import org.wanna.jabbot.messaging.MessageContent;
import org.wanna.jabbot.messaging.Resource;
import org.wanna.jabbot.messaging.RoutableMessageContent;
import org.wanna.jabbot.messaging.RxMessage;
import org.wanna.jabbot.messaging.TxMessage;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.CommandParsingResult;
import org.wanna.jabbot.event.EventDispatcher;

import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class MessageEventHandler implements EventHandler<MessageEvent>{
	private final Logger logger = LoggerFactory.getLogger(MessageEventHandler.class);
	private final Map<String,Binding> bindingMap;

	public MessageEventHandler(Map<String,Binding> bindingMap) {
		this.bindingMap  = bindingMap;
	}

	@Override
	public boolean process(MessageEvent event, EventDispatcher dispatcher) {
		final Binding binding = event.getBinding();
		final RxMessage request = event.getPayload();
		final MessageContent messageContent = (request == null ? null : request.getMessageContent());

		if(messageContent == null){
			return false;
		}

		logger.debug("[JABBOT] received request from {}: {}",request.getSender(), messageContent.getBody());
		MessageContent commandResult = executeCommand(binding,request.getSender(),messageContent);
		if(commandResult == null || commandResult.getBody().isEmpty()){
			return false;
		}

		OutgoingMessageEvent outgoingMessageEvent;
		if(commandResult instanceof RoutableMessageContent){
			MessageContent innerResult = executeCommand(binding,request.getSender(),commandResult);
			TxMessage response = new DefaultTxMessage((innerResult == null ? commandResult : innerResult),
					new DefaultResource(((RoutableMessageContent) commandResult).getResourceId(),""), request);
			outgoingMessageEvent = new OutgoingMessageEvent(bindingMap.get(((RoutableMessageContent) commandResult).getBindingId()),response);
		}else{
			TxMessage response = new DefaultTxMessage(commandResult,request.getSender(),request);
			outgoingMessageEvent = new OutgoingMessageEvent(binding,response);
		}

		dispatcher.dispatch(outgoingMessageEvent);
		return true;
	}

	/**
	 * Check if a message is a Command or not.
	 * If it's a command ,command gets executed and resulting MessageContent is returned.
	 * Otherwise the method will return NULL
	 *
	 * @param binding the binding on which the message has been received
	 * @param sender the resource which generated the message
	 * @param content content of the received message
	 * @return Command result, or NULL if invalid command
	 */
	private MessageContent executeCommand(Binding binding, Resource sender, MessageContent content){
		//Check if message content starts with command prefix. if not abort early.
		final String commandPrefix = binding.getConfiguration().getCommandPrefix();
		if(!content.getBody().startsWith(commandPrefix)){
			return null;
		}

		final CommandParser commandParser = new DefaultCommandParser(commandPrefix);
		final CommandParsingResult result = commandParser.parse(content.getBody());
		final DefaultCommandMessage commandMessage = new DefaultCommandMessage(sender,result.getRawArgsLine());

		try {
			final Command command = CommandManager.getInstanceFor(binding).getCommandFactory().create(result.getCommandName());
			return command.process(commandMessage);
		} catch (CommandNotFoundException e) {
			logger.debug("command not found: '{}'", e.getCommandName());
			return null;
		}
	}
}
