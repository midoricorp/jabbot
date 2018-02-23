package org.wanna.jabbot;

import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.event.*;
import org.wanna.jabbot.config.FileConfigurationDao;
import org.wanna.jabbot.config.JabbotConfiguration;
import org.wanna.jabbot.event.EventManager;
import org.wanna.jabbot.event.handlers.*;
import org.wanna.jabbot.extension.ExtensionScanner;
import org.wanna.jabbot.web.WebServer;

import java.io.File;
import java.util.concurrent.*;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot implements Daemon{
	private final Logger logger = LoggerFactory.getLogger(Jabbot.class);
	private final static String CONFIG_FILE = "jabbot.json";
	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
		new ThreadFactoryBuilder().setNameFormat("binding monitoring - %d").setDaemon(true).build()
	);
	private final EventManager eventManager;
	private WebServer webServer;

	public Jabbot(){
		//Install slf4j bridge
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		eventManager = EventManager.getInstance();
	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(DaemonContext context) throws DaemonInitException{
		logger.info("---- Initializing Jabbot ----");
		logger.info("# Loading configuration file");
		//File file = new File(CONFIG_FILE);
		File file = new File(ClassLoader.getSystemResource(CONFIG_FILE).getFile());
		FileConfigurationDao fileConfigurationDao = new FileConfigurationDao(file);
		JabbotConfiguration configuration = fileConfigurationDao.getConfiguration();
		logger.info("# Scanning extensions folder");
		if(configuration.getExtensionsFolder() != null){
			new ExtensionScanner(configuration.getExtensionsFolder()).run();
		}
		logger.info("# Registering configured bindings");
		if(configuration.getServerList() != null) {
			for (final BindingConfiguration connectionConfiguration : configuration.getServerList()) {
				BindingContainer.create(connectionConfiguration);
			}
		}
		logger.info("# Initializing Event handlers");
		this.registerEventHandlers();

		if(configuration.getWebServer() != null) {
			logger.info("# Initializing web container");
			this.webServer = WebServer.initialize(configuration.getWebServer());
		}

		logger.info("### Initialization completed");
	}

	@Override
	public void start(){
		logger.info("---- Jabbot is now starting ----");
		logger.info("# Starting event services");
		eventManager.start();
		logger.info("# Starting Binding monitoring");
		final long delay = 60L;
		final TimeUnit unit = TimeUnit.SECONDS;
		scheduledExecutorService.scheduleAtFixedRate(new BindingMonitor(BindingContainer.getRegistry()),
				0L,delay, TimeUnit.SECONDS);
		logger.debug("Binding monitoring is scheduled to run every {} {}",delay,unit);

		logger.info("# Starting embedded web server");
		if(webServer != null){
			webServer.start();
		}

		logger.info("### Startup completed");
	}

	@Override
	public void stop(){
		webServer.stop();
		eventManager.stop();
		scheduledExecutorService.shutdown();
	}

	/**
	 * Feeds EventHandlerFactory.
	 * It maps binding Event to Jabbot events handler
	 */
	private void registerEventHandlers(){
		EventHandlerFactory factory = EventHandlerFactory.getInstance();
		factory.register(ConnectedEvent.class, new ConnectedEventHandler());
		factory.register(MessageEvent.class, new MessageEventHandler());
		factory.register(ConnectionRequestEvent.class,new ConnectionRequestEventHandler());
		factory.register(JoinRoomEvent.class,new JoinRoomEventHandler());
		factory.register(OutgoingMessageEvent.class, new OutgoingMessageEventHandler());
		factory.register(RoomInviteEvent.class, new RoomInviteHandler());
		factory.register(DisconnectionRequestEvent.class, new DisconnectionRequestEventHandler());
		factory.register(DisconnectedEvent.class,new DisconnectedEventHandler());
		factory.register(ServletRegistrationEvent.class, new ServletRegistrationEventHandler());
	}

	public static void main(String[] args) throws Exception{
		final Jabbot jabbot = new Jabbot();
		jabbot.init(null);
		jabbot.start();
		while (true) {
			Thread.sleep(1000L);
		}
	}
}
