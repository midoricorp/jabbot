package org.wanna.jabbot.web.config;

import java.util.List;

public class WebServerConfig {
	private int port;
	private List<User> users;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
