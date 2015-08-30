package org.wanna.jabbot.command.config;

import org.wanna.jabbot.binding.config.ExtensionConfiguration;

import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-20
 */
public class CommandConfig implements ExtensionConfiguration{
	private String name,className,helpMessage;
	private Map<String,Object> configuration;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map<String, Object> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Object> configuration) {
		this.configuration = configuration;
	}

    @Override
    public Type getType() {
        return Type.COMMAND;
    }

    public String getHelpMessage() {
		return helpMessage;
	}

	public void setHelpMessage(String helpMessage) {
		this.helpMessage = helpMessage;
	}
}
