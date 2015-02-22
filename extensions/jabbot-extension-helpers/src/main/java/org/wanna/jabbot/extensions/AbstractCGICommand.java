package org.wanna.jabbot.extensions;

import org.wanna.jabbot.command.CommandResult;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.config.CommandConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-22
 */
public abstract class AbstractCGICommand extends AbstractCommandAdapter{

	protected AbstractCGICommand(CommandConfig configuration) {
		super(configuration);
	}

	public abstract String getScriptName();

	@Override
	public final CommandResult process(MessageWrapper message) {
		String[] envp = { "JABBOT_ACTION=run", "JABBOT_COMMAND=" + getCommandName(), "JABBOT_FROM=" + message.getSender() };
		List<String> argList =  message.getArgs();

		//add script to run as arg0
		argList.add(0, getScriptName());
		String[] command = argList.toArray(new String[argList.size()]);

		String response = exec(command, envp);
		CommandResult result = new CommandResult();
		result.setText(response);
		return result;
	}


	@Override
	public final String getHelpMessage() {
		String[] command = { getScriptName() };
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
			e.printStackTrace();
		}

		return output.toString();
	}
}
