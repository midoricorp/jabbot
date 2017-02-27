package org.wanna.jabbot.extensions.say;

import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.messaging.DefaultRoutableMessageContent;
import org.wanna.jabbot.messaging.MessageContent;

import java.util.List;

public class SayCommand extends AbstractCommandAdapter{
	private static final String DELIMITER = ":";

	public SayCommand(String commandName) {
		super(commandName);
	}

	@Override
	public MessageContent process(CommandMessage message) {
		List<String> args = getArgsParser().parse(message.getArgsLine());
		if( args == null || args.size() < 2){
			//Not enough args. returns a proper error message to user
			return null;
		}

		//Extract location information here
		String location = args.get(0);
		final int index = location.indexOf(DELIMITER);
		final String protocol = getProtocol(location,index);
		final String resource = getResource(location,index);
		final StringBuilder sb = new StringBuilder();

		for(int pos = 1; pos < args.size(); pos++){
			sb.append(args.get(pos)).append(" ");
		}

		return new DefaultRoutableMessageContent(protocol,resource, sb.toString());
	}

	private String getProtocol(String argLine, int index){
		return argLine.substring(0,index);
	}

	private String getResource(String argLine, int index){
		return argLine.substring(index+1 + 1, argLine.length());
	}
}
