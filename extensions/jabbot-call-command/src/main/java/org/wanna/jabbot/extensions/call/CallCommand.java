package org.wanna.jabbot.extensions.call;

import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.extensions.AbstractCommand;


/**
 * @author snacar <snacar>
 * @since 2015-01-15
 */
public class CallCommand extends AbstractCommand{

	public CallCommand(String commandName) {
		super(commandName);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String[] args = getParsedCommand().getArgs();
		String baseurl = "http://voxcall.me";
		if(args != null && args.length > 0){
			String callee = args[0];
			String response = String.format("Call %s at %s/%s",callee, baseurl, callee);
			chatroom.sendMessage(response);
		}
		else{
			String response = String.format("please add someone to call, you idiot");
			chatroom.sendMessage(response);
		}
	}
}
