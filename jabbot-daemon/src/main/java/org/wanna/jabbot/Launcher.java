package org.wanna.jabbot;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.wanna.jabbot.config.JabbotConfig;
import org.wanna.jabbot.config.PluginConfigurator;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-29
 */
public class Launcher implements Daemon{
	final Logger logger = LoggerFactory.getLogger(Launcher.class);
	Jabbot jabbot;

	@Override
	public void init(DaemonContext daemonContext) throws DaemonInitException{
		logger.debug("initializing...");
		ApplicationContext ctx =
				new AnnotationConfigApplicationContext(
						JabbotConfig.class,
						PluginConfigurator.class
				);
		jabbot = (Jabbot)ctx.getBean("jabbot");
	}

	@Override
	public void start() throws Exception {
		jabbot.connect();
		jabbot.initRooms();
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void stop() throws Exception {

	}

	@Override
	public void destroy() {

	}

	public static void main(String args[]) throws Exception {
		Launcher launcher = new Launcher();
		launcher.init(null);
		launcher.start();
	}
}
