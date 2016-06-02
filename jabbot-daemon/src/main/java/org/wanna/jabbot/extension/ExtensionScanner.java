package org.wanna.jabbot.extension;

import java.io.File;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ExtensionScanner implements Runnable{
	private final File extensionPath;

	public ExtensionScanner(String extensionPath){
		this.extensionPath = new File(extensionPath);
	}

	@Override
	public void run() {
		if(!extensionPath.exists() || !extensionPath.canRead() || extensionPath.isFile()){
			return;
		}

		File[] extensions = extensionPath.listFiles();
		if(extensions == null){
			return;
		}

		for (File extension : extensions) {
			ExtensionPoint extensionPoint = new ExtensionPoint(extension);
			if(extensionPoint.isValid()){
				ExtensionLoader.getInstance().load(extensionPoint);
			}
		}
	}
}
