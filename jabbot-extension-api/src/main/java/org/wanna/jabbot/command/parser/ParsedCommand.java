package org.wanna.jabbot.command.parser;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
public class ParsedCommand {
	private String commandName;
	private String[] args;

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
}
