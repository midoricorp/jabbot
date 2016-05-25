package org.wanna.jabbot.extensions.cgi;

import org.wanna.jabbot.command.AbstractCGICommand;

import java.util.Map;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class CGICommand extends AbstractCGICommand {
	private String script = null;

	public CGICommand(String commandName) {
		super(commandName);
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;
		script = configuration.get("script").toString();
	}

	@Override
	public String getScriptName() {
		return script;
	}
}
