package org.wanna.jabbot.binding.config;

import org.wanna.jabbot.command.config.CommandConfig;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration class for a {@link org.wanna.jabbot.binding.Binding}
 *
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class BindingConfiguration {
	private String type;
	private String url,serverName;
	private String username, password,identifier;
	private String commandPrefix;
	private int port;
	private List<RoomConfiguration> rooms;
	private Set<CommandConfig> commands;
	private Map<String,Object> parameters;
	private boolean debug;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public List<RoomConfiguration> getRooms() {
		return rooms;
	}

	public void setRooms(List<RoomConfiguration> rooms) {
		this.rooms = rooms;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Set<CommandConfig> getCommands() {
		return commands;
	}

	public void setCommands(Set<CommandConfig> commands) {
		this.commands = commands;
	}

	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
