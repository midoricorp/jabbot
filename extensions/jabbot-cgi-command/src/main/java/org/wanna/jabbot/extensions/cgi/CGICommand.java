package org.wanna.jabbot.extensions.cgi;

import org.wanna.jabbot.command.CommandResult;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.*;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class CGICommand extends AbstractCommandAdapter {


	private String script = null;

	public CGICommand(CommandConfig configuration) {
		super(configuration);
	 
		
	}

	private String exec(String[] command, String[] envp) {
		StringBuffer output = new StringBuffer();
		try {
			Process cmd = Runtime.getRuntime().exec(command, envp);
			cmd.waitFor();
			BufferedReader reader = 
                           new BufferedReader(new InputStreamReader(cmd.getInputStream()));
 
			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
	}

	@Override
	public String getHelpMessage() {
		String[] command = { script };
		String[] envp = { "JABBOT_ACTION=help", "JABBOT_COMMAND=" + getCommandName() };


		return exec(command, envp);
	}

	@Override
	public CommandResult process(MessageWrapper message) {
		String[] envp = { "JABBOT_ACTION=run", "JABBOT_COMMAND=" + getCommandName(), "JABBOT_FROM=" + message.getSender() };
		List<String> argList =  message.getArgs();

		//add script to run as arg0
		argList.add(0, script);
		String[] command = argList.toArray(new String[argList.size()]);

		String response = exec(command, envp);
		CommandResult result = new CommandResult();
		result.setText(response);
		return result;
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;

		script = configuration.get("script").toString();

	}
}
