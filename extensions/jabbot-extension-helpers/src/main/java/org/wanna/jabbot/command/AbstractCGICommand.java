package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-22
 */
public abstract class AbstractCGICommand extends AbstractCommandAdapter {
	private final Logger logger = LoggerFactory.getLogger(AbstractCGICommand.class);

	protected AbstractCGICommand(CommandConfig configuration) {
		super(configuration);
	}

	public abstract String getScriptName();

	@Override
	public final DefaultCommandMessage process(CommandMessage message) {
		String[] envp = { "JABBOT_ACTION=run", "JABBOT_COMMAND=" + getCommandName(), "JABBOT_FROM=" + message.getSender() };
		List<String> argList =  super.getArgsParser().parse(message.getBody());
		String script = getFilePath(getScriptName());
		if(script == null){
			return null;
		}
		//add script to run as arg0
		argList.add(0, script);
		String[] command = argList.toArray(new String[argList.size()]);

		String response = exec(command, envp);
		return new DefaultCommandMessage(response);
	}


	@Override
	public final String getHelpMessage() {
		String script = getFilePath(getScriptName());
		if(script == null){
			return null;
		}
		String[] command = { script };
		String[] envp = { "JABBOT_ACTION=help", "JABBOT_COMMAND=" + getCommandName() };


		return exec(command, envp);
	}

	private String getFilePath(String scriptName){
		URL url = ClassLoader.getSystemResource(scriptName);
		if(url == null){
			logger.warn("unable to find {} in classpath",scriptName);
			return null;
		}else{
			return url.getFile();
		}
	}

	private String exec(String[] command, String[] envp) {
		StringBuilder output = new StringBuilder();
		try {
			Process cmd = Runtime.getRuntime().exec(command, envp);
			cmd.waitFor();
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(cmd.getInputStream()));

			String line;
			while ((line = reader.readLine())!= null) {
				output.append(line).append('\n');
			}

		} catch (Exception e) {
			logger.error("error executing {}",getScriptName(),e);
		}

		return output.toString();
	}
}
