package org.wanna.jabbot.config;

import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;

import java.util.Collection;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-09
 */
public class JabbotConfiguration {
	private String extensionsFolder;
	private Collection<ExtensionConfiguration> bindings;
	private Collection<BindingConfiguration> serverList;

	public String getExtensionsFolder() {
		return extensionsFolder;
	}

	public void setExtensionsFolder(String extensionsFolder) {
		this.extensionsFolder = extensionsFolder;
	}

	public Collection<BindingConfiguration> getServerList() {
		return serverList;
	}
	public void setServerList(Collection<BindingConfiguration> serverList) {
		this.serverList = serverList;
	}

	public Collection<ExtensionConfiguration> getBindings() {
		return bindings;
	}

	public void setBindings(List<ExtensionConfiguration> bindings) {
		this.bindings = bindings;
	}
}
