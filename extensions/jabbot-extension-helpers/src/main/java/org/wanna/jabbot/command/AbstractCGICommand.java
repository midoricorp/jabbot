package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.messaging.DefaultMessageContent;
import org.wanna.jabbot.messaging.MessageContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-22
 */
public abstract class AbstractCGICommand extends AbstractCommandAdapter {
	private final Logger logger = LoggerFactory.getLogger(AbstractCGICommand.class);

	protected AbstractCGICommand(String commandName) {
		super(commandName);
	}

	public abstract String getScriptName();

	@Override
	public final MessageContent process(CommandMessage message) {
		String[] envp = { "JABBOT_ACTION=run", "JABBOT_COMMAND=" + getCommandName(), "JABBOT_FROM=" + message.getSender() };
		List<String> argList =  super.getArgsParser().parse(message.getArgsLine());

		File script = new File(getScriptName());
		if(!script.exists() || !script.isFile() || !script.canRead()){
			logger.warn("could not execute {}",getScriptName());
			return null;
		}

		//add script to run as arg0
		argList.add(0, script.getPath());
		String[] command = argList.toArray(new String[argList.size()]);

		String response = exec(command, envp);
		return new DefaultMessageContent(response);
	}


	@Override
	public final String getHelpMessage() {
		File script = new File(getScriptName());
		if(!script.exists() || !script.isFile() || !script.canRead()){
			logger.warn("could not execute {}",getScriptName());
			return null;
		}

		String[] command = { script.getPath() };
		String[] envp = { "JABBOT_ACTION=help", "JABBOT_COMMAND=" + getCommandName() };


		return exec(command, envp);
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
