package org.wanna.jabbot.extensions.call;

import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.util.Map;


/**
 * @author snacar <snacar>
 * @since 2015-01-15
 */
public class CallCommand extends AbstractCommand{
	private String baseUrl = null;
	private String missingUserMessage = "please add someone to call, you idiot";

	public CallCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		if(baseUrl == null){
			chatroom.sendMessage("Call command not configured: missing base_url");
		}
		String[] args = getParsedCommand().getArgs();
		if(args != null && args.length > 0){
			String callee = args[0];
			String response = String.format("Call %s at %s/%s",callee, baseUrl, callee);
			chatroom.sendMessage(response);
		}
		else{
			String response = String.format(missingUserMessage);
			chatroom.sendMessage(response);
		}
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if(configuration == null ) return;
		if(configuration.containsKey("url")){
			this.baseUrl = String.valueOf(configuration.get("url"));
		}

		if(configuration.containsKey("missing_user")){
			this.missingUserMessage = String.valueOf(configuration.get("missing_user"));
		}
	}
}
