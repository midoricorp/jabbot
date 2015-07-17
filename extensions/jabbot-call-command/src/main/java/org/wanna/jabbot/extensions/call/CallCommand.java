package org.wanna.jabbot.extensions.call;

import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;
import org.wanna.jabbot.command.config.CommandConfig;

import java.util.List;
import java.util.Map;


/**
 * @author snacar <snacar>
 * @since 2015-01-15
 */
public class CallCommand extends AbstractCommandAdapter {
	private String baseUrl = null;
	private String missingUserMessage = "please add someone to call, you idiot";

	public CallCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public Message process(Message message) {
		DefaultMessage result = new DefaultMessage();
		if(baseUrl == null){
			result.setBody("Call command not configured: missing base_url");
		}
		List<String> args =  getArgsParser().parse(message.getBody());
		if(args.size() > 0){
			String callee = args.get(0);
			String response = String.format("Call %s at %s/%s",callee, baseUrl, callee);
			result.setBody(response);
		}
		else{
			String response = String.format(missingUserMessage);
			result.setBody(response);
		}
		return result;
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
