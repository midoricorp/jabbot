package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingCreationException;
import org.wanna.jabbot.binding.BindingFactory;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.BindingDefinition;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.config.JabbotConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-30
 */
public class Jabbot {
	final Logger logger = LoggerFactory.getLogger(Jabbot.class);

	private JabbotConfiguration configuration;
	private BindingFactory bindingFactory;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

	private List<Binding> bindings = new ArrayList<>();

	public Jabbot( JabbotConfiguration configuration ) {
		this.configuration = configuration;
		this.bindingFactory = newConnectionFactory(configuration.getBindings());
		scheduledExecutorService.scheduleAtFixedRate(new BindingMonitor(), 10L,10L, TimeUnit.SECONDS);
	}

	public boolean connect(){
		for (final BindingConfiguration connectionConfiguration : configuration.getServerList()) {
			final Binding conn;
			try {
				conn = bindingFactory.create(connectionConfiguration);
				CommandManager.getInstanceFor(conn).initializeFromConfigSet(connectionConfiguration.getExtensions());
                conn.registerListener(new JabbotBindingListener(conn,connectionConfiguration.getCommandPrefix()));
				bindings.add(conn);

				//conn.connect(connectionConfiguration);
				/*
				for (RoomConfiguration roomConfiguration : connectionConfiguration.getRooms()) {
					conn.joinRoom(roomConfiguration);
				}
*/
				if(conn.isConnected()){
					logger.debug("connection established to {} as {}",connectionConfiguration.getUrl(),connectionConfiguration.getUsername());
				}
			} catch (BindingCreationException e) {
				logger.error("failed to create binding for {}",connectionConfiguration.getType(),e);
			}
		}
		return true;
	}

	public void disconnect(){
	}

	private BindingFactory newConnectionFactory(Collection<BindingDefinition> bindings){
		BindingFactory factory =  new JabbotBindingFactory();
		if(bindings == null){
			return factory;
		}

		for (BindingDefinition binding : bindings) {
			try {
				Class clazz = Class.<Command>forName(String.valueOf(binding.getClassName()));
                @SuppressWarnings("unchecked")
				Class<? extends Binding> connectionClass = (Class<? extends Binding>)clazz;
				logger.info("registering {} binding with class {}",binding.getName(),binding.getClassName());
				factory.register(binding.getName(),connectionClass);
			} catch (ClassNotFoundException e) {
				logger.error("unable to register {} binding with class {}",binding.getName(),binding.getClassName());
			}
		}
		return factory;
	}

	private class BindingMonitor implements Runnable{

		@Override
		public void run() {
			logger.debug("checking binding health");
			for (final Binding binding : bindings) {
				try{
					if(!binding.isConnected()){
						logger.info("binding {} is disconnected. starting...",binding);
						executorService.submit(new Runnable() {
							@Override
							public void run() {
								try {
									binding.connect(null);
								}catch (Exception e){
									logger.error("cannot start binding {}",binding,e);
								}
							}
						});
					}else{
						logger.debug("binding {} is connected",binding);
					}

				}catch (Throwable t){
					logger.error("unable to check {} health",binding,t);
				}
			}
			logger.debug("health check done");
		}
	}
}
