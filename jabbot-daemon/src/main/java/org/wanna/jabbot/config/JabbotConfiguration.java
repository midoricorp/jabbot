package org.wanna.jabbot.config;

import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.JabbotConnectionConfiguration;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class JabbotConfiguration {
	private List<BindingConfiguration> bindings;
	private List<JabbotConnectionConfiguration> serverList;

	public List<JabbotConnectionConfiguration> getServerList() {
		return serverList;
	}
	public void setServerList(List<JabbotConnectionConfiguration> serverList) {
		this.serverList = serverList;
	}

	public List<BindingConfiguration> getBindings() {
		return bindings;
	}

	public void setBindings(List<BindingConfiguration> bindings) {
		this.bindings = bindings;
	}
}
