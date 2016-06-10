package org.wanna.jabbot.extensions.cgi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCGICommand;

import java.net.URL;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class ClassPathCGICommand extends AbstractCGICommand{
	private final Logger logger = LoggerFactory.getLogger(ClassPathCGICommand.class);

	public ClassPathCGICommand(String commandName) {
		super(commandName);
	}

	@Override
	public String getScriptPath(String script) {
		URL url = ClassLoader.getSystemResource(script);
		if(url == null){
			logger.warn("unable to find {} in classpath",script);
			return null;
		}else{
			return url.getFile();
		}
	}

}
