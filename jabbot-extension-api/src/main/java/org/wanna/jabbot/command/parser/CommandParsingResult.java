package org.wanna.jabbot.command.parser;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-18
 */
public class CommandParsingResult {
	private String commandName;
	private String rawArgsLine;

	public CommandParsingResult(String commandName, String rawArgsLine) {
		this.commandName = commandName;
		this.rawArgsLine = rawArgsLine;
	}

	public String getCommandName() {
		return commandName;
	}

	public String getRawArgsLine() {
		return rawArgsLine;
	}
}
