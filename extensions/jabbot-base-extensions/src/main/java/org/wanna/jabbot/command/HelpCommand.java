package org.wanna.jabbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class HelpCommand extends AbstractCommandAdapter implements CommandFactoryAware {
	private CommandFactory commandFactory;
	final Logger logger = LoggerFactory.getLogger(HelpCommand.class);
	final String NO_HELP_MESSAGE = "No help available for command";

	public HelpCommand(final CommandConfig config){
		super(config);
	}

	@Override
	public Message process(CommandMessage message) {
		List<String> args =  getArgsParser().parse(message.getBody());
		StringBuilder sb = new StringBuilder();
		if(args.size() > 0){
			String commandName = args.get(0);
			if(commandFactory.getAvailableCommands().containsKey(commandName)){
				Command command = commandFactory.getAvailableCommands().get(commandName);
				if(command.getHelpMessage() == null){
					sb.append(NO_HELP_MESSAGE)
							.append("'").append(command.getCommandName()).append("'");
				}else{
					sb.append(command.getCommandName()).append(":\n");
					sb.append(command.getHelpMessage());
				}
			}else{
				sb.append("no registered command found with name '").append(commandName).append("'");
			}
		}else{
			sb.append("Below is the list of available commands:\n");
			for (Command command : commandFactory.getAvailableCommands().values()) {
				sb.append(command.getCommandName());
				if(command.getDescription() != null){
					sb.append(command.getDescription());
				}
				sb.append("\n");
			}
			sb.append("\n").append("more help can be obtained using 'help <commandName>'");
		}
		DefaultCommandMessage result = new DefaultCommandMessage();
		result.setBody(sb.toString());
		return result;
	}

	@Override
	public void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
}
