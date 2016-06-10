package org.wanna.jabbot.extensions.cgi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCGICommand;

import java.io.File;

/**
 * @author tsearle 
 * @since 2015-02-21
 */
public class FileCGICommand extends AbstractCGICommand {
	private final Logger logger = LoggerFactory.getLogger(FileCGICommand.class);

	public FileCGICommand(String commandName) {
		super(commandName);
	}

	@Override
	public String getScriptPath(String script) {
		File file = new File(script);
		if(!file.exists() || !file.isFile() || !file.canRead()){
			logger.warn("could not execute {}", script);
			return null;
		}
		return file.getPath();
	}
}
