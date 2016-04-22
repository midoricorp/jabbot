package org.wanna.jabbot.binding.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * @author tsearle <tsearle>
 * @since 2016-04-08
 */
public class SparkBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(SparkBinding.class);
	private Hashtable<String,Room> roomMap = null;
	com.ciscospark.Spark spark = null;
	boolean useWebhook = false;
	String webhookUrl = "";
	com.ciscospark.SparkServlet sparkServlet = null;
	private boolean connected;

	public SparkBinding(BindingConfiguration configuration) {
		super(configuration);
		roomMap = new Hashtable<String,Room>();

		if (configuration.getParameters() != null) {
			if (configuration.getParameters().containsKey("use_webhook")) {
				useWebhook = (boolean)configuration.getParameters().get("use_webhook");
			}
			if (configuration.getParameters().containsKey("webhook_url")) {
				webhookUrl = (String)configuration.getParameters().get("webhook_url");
			}
		}
	}

	@Override
	public boolean connect(BindingConfiguration configuration) {
		spark = com.ciscospark.Spark.builder()
			.baseUrl(URI.create(getConfiguration().getUrl()))
			.accessToken(getConfiguration().getPassword())
			.build();
		if(useWebhook) {
			Server server = new Server(8080);
			ServletHandler context = new ServletHandler();
			server.setHandler(context);

			sparkServlet = new com.ciscospark.SparkServlet();
			context.addServletWithMapping(new ServletHolder(sparkServlet), "/*");

			try {
				server.start();
			} catch (Exception e) {
				logger.error("Unable to start server: ", e);
				return false;
			}

			// first cleanup old hooks
			Iterator<com.ciscospark.Webhook> webhooks = spark.webhooks().iterate();
			while(webhooks.hasNext()) {
				com.ciscospark.Webhook hk  = webhooks.next();
				logger.info("deleting webhook " + hk.getName() + " id: " + hk.getId());
				spark.webhooks().path("/"+hk.getId()).delete();
			}


			connected = true;
		}else{
			connected = true;
		}

		for (RoomConfiguration roomConfiguration : getConfiguration().getRooms()) {
			joinRoom(roomConfiguration);
		}
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		logger.debug("Joining room " + configuration.getName());
		Room room = new SparkRoom(this,listeners);
		roomMap.put(configuration.getName(),room);
		room.join(configuration);
		return room;
	}

	@Override
	public boolean isConnected() {
		if(useWebhook){
			return false;
		}else{
			return connected;
		}
	}

	@Override
	public Room getRoom(String roomName) {
		return roomMap.get(roomName);
	}
}
