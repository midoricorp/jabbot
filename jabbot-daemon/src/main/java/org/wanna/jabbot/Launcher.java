package org.wanna.jabbot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.wanna.jabbot.config.JabbotConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-29
 */
public class Launcher implements Daemon{
	final Logger logger = LoggerFactory.getLogger(Launcher.class);
	private final static String CONFIG_FILE = "jabbot.json";
	private Jabbot jabbot;

	@Override
	public void init(DaemonContext daemonContext) throws DaemonInitException{
		logger.info("initializing...");
		//Install slf4j bridge
		SLF4JBridgeHandler.uninstall();
		SLF4JBridgeHandler.install();
		java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);
		//Load and parse Jabbot json config file
		InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILE);
		JabbotConfiguration jabbotConfiguration = newConfiguration(is);
		//Create Jabbot instance
		jabbot = new Jabbot(jabbotConfiguration);
		logger.info("initialization completed.");
	}

	@Override
	public void start() throws Exception {
		jabbot.connect();
	}

	@Override
	public void stop() throws Exception {
		logger.info("Stopping Jabbot service");
		jabbot.disconnect();
	}

	@Override
	public void destroy() {

	}

	private JabbotConfiguration newConfiguration(InputStream is){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(is,JabbotConfiguration.class);
		} catch (IOException e) {
			logger.error("unable to read json config file",e);
			return null;
		}

	}

	/**
	 * Main method allowing to easily run the bot in an IDE
	 *
	 * @param args command line args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		Launcher launcher = new Launcher();
		launcher.init(null);
		launcher.start();

		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw e;
			}
		}

	}
}
