package org.wanna.jabbot;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.wanna.jabbot.config.JabbotConfig;
import org.wanna.jabbot.config.PluginConfigurator;

import java.util.logging.Level;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-29
 */
public class Launcher implements Daemon{
	final Logger logger = LoggerFactory.getLogger(Launcher.class);
	Jabbot jabbot;

	@Override
	public void init(DaemonContext daemonContext) throws DaemonInitException{
		logger.info("initializing...");
		//Install slf4j bridge
		SLF4JBridgeHandler.uninstall();
		SLF4JBridgeHandler.install();
		java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);

		//Bootstrap Spring context
		ApplicationContext ctx =
				new AnnotationConfigApplicationContext(
						JabbotConfig.class,
						PluginConfigurator.class
				);
		//Get a jabbot instance
		jabbot = (Jabbot)ctx.getBean("jabbot");
		logger.info("initialization completed.");
	}

	@Override
	public void start() throws Exception {
		jabbot.connect();
		jabbot.initRooms();
	}

	@Override
	public void stop() throws Exception {
		logger.info("Stopping Jabbot service");
		jabbot.disconnect();
	}

	@Override
	public void destroy() {

	}

	public static void main(String args[]) throws Exception {
		Launcher launcher = new Launcher();
		launcher.init(null);
		launcher.start();

		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw e;
			}
		}

	}
}
