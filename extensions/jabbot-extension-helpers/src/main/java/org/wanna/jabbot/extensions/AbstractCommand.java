package org.wanna.jabbot.extensions;

import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.parser.ParsedCommand;

import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public abstract class AbstractCommand implements Command {
	private ParsedCommand parsedCommand;
	private final String commandName;
	private final CommandConfig configuration;

	protected AbstractCommand(final CommandConfig configuration){
		this.configuration = configuration;
		this.commandName = configuration.getName();
		if(configuration.getConfiguration() != null){
			configure(configuration.getConfiguration());
		}
	}

	public ParsedCommand getParsedCommand() {
		return parsedCommand;
	}

	public void setParsedCommand(ParsedCommand parsedCommand) {
		this.parsedCommand = parsedCommand;
	}

	public final String getCommandName() {
		return commandName;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getHelpMessage() {
		return null;
	}

	public final CommandConfig getConfiguration() {
		return configuration;
	}

	/**
	 * Override this method if you want to process entries from the configuration map
	 * @param configMap config map which is passed to the json command config
	 */
	public void configure(Map<String,Object> configMap){

	}
}
