package org.wanna.jabbot.extensions;

import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.parser.ParsedCommand;

import java.util.Map;

/**
 * AbstractCommandAdapter is an adapter class which purpose is to
 * ease the development of new extension.
 *
 * It provides some basic functionality such as implementing the Configurable interface
 * to enable customization of the command via a map parameter
 * as well as provide a "blank" implementation of most of the method from the Command interface
 * enabling extensions to only override needed method.
 *
 * @see {@link org.wanna.jabbot.command.Command}
 * @see {@link org.wanna.jabbot.command.behavior.Configurable}
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public abstract class AbstractCommandAdapter implements Command, Configurable {
	private ParsedCommand parsedCommand;
	private final String commandName;
	private final CommandConfig configuration;

	/**
	 * Default constructor which takes the command configuration as parameter.
	 *
	 * @param configuration Command configuration object
	 */
	protected AbstractCommandAdapter(final CommandConfig configuration){
		this.configuration = configuration;
		this.commandName = configuration.getName();
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
