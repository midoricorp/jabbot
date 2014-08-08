package org.wanna.jabbot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.JabbotCommandFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
@Configuration
@Import(CommandConfig.class)
public class PluginConfigurator{
	final Logger logger = LoggerFactory.getLogger(PluginConfigurator.class);

	@Bean(name="pluginContext")
	public ClassPathXmlApplicationContext newPluginContext(){
		return new ClassPathXmlApplicationContext(new String[]{
				"classpath*:jabbot-extension.xml"
		});
	}

	@Bean
	public Map<String,Command> getRegisteredCommands( JabbotCommandFactory commandFactory,@Qualifier("pluginContext") ClassPathXmlApplicationContext context){
		Collection<Command> commands = context.getBeansOfType(Command.class).values();
		Map<String,Command> result = new HashMap<>();
		for (Command command : commands) {
			context.getAutowireCapableBeanFactory().autowireBean(command);
			result.put(command.getCommandName(),command);
		}
		logger.debug("loaded {} command extensions",context.getBeansOfType(Command.class).size());
		commandFactory.setRegistry(result);
		return result;
	}
}
