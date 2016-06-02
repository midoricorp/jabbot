package org.wanna.jabbot.extension;

import java.io.File;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ExtensionPoint {
	private final File extensionFolder;
	private final File configFile;
	private final File libFolder;
	private long lastModified;

	public ExtensionPoint(File extensionFolder){
		this.extensionFolder = extensionFolder;
		this.configFile = new File(extensionFolder.getPath()+"/config.json");
		this.libFolder = new File(extensionFolder.getPath()+"/lib/");
		this.lastModified = extensionFolder.lastModified();
	}

	public boolean isValid(){
		//Validate extension folder
		if(!extensionFolder.exists() || !extensionFolder.canRead() || !extensionFolder.isDirectory()){
			return false;
		}

		if(!configFile.exists() || !configFile.canRead() || !configFile.isFile()){
			return false;
		}

		if(!libFolder.exists() || !libFolder.canRead() || !libFolder.isDirectory()){
			return false;
		}

		return true;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getExtensionFolder() {
		return extensionFolder;
	}

	public long getLastModified() {
		return lastModified;
	}

	public File getLibFolder() {
		return libFolder;
	}
}
