package org.wanna.jabbot.config;

import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.BindingDefinition;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class JabbotConfiguration {
	private List<BindingDefinition> bindings;
	private List<BindingConfiguration> serverList;

	public List<BindingConfiguration> getServerList() {
		return serverList;
	}
	public void setServerList(List<BindingConfiguration> serverList) {
		this.serverList = serverList;
	}

	public List<BindingDefinition> getBindings() {
		return bindings;
	}

	public void setBindings(List<BindingDefinition> bindings) {
		this.bindings = bindings;
	}
}
