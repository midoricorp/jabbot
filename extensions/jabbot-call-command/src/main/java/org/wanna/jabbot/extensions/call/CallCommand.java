package org.wanna.jabbot.extensions.call;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.extensions.AbstractCommand;


/**
 * @author snacar <snacar>
 * @since 2015-01-15
 */
public class CallCommand extends AbstractCommand{
	private Logger logger = LoggerFactory.getLogger(CallCommand.class);

	public CallCommand(String commandName) {
		super(commandName);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String[] args = getParsedCommand().getArgs();
		String baseurl = "https://voxbone-click2call.rhcloud.com";
		String callee = args[0];
		String response = String.format("Call %s at %s/%s",callee, baseurl, callee);
		chatroom.sendMessage(response);
	}
}
