package org.wanna.jabbot.config;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamSource;
import org.wanna.jabbot.Chatroom;
import org.wanna.jabbot.Jabbot;
import org.wanna.jabbot.command.CommandManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-29
 */
@Configuration
@Import(CommandConfig.class)
@PropertySource("classpath:/jabbot.properties")
@ImportResource("classpath:/chatrooms.xml")
public class JabbotConfig {

	@Autowired
	Environment env;
	@Autowired @Qualifier("chatrooms")
	ArrayList chatroomList;


	InputStreamSource avatar;

	/**
	 * Returns a Configured instance of XmppConnection
	 * based on a property file
	 *
	 * Example config;
	 * xmpp.host=value
	 * xmpp.port=value
	 * xmpp.serviceName=value
	 */
	@Bean
	public ConnectionConfiguration newConnectionConfiguration(){
		final String host = env.getProperty("xmpp.host");
		final int port = env.getProperty("xmpp.port",Integer.class);
		final String serviceName = env.getProperty("xmpp.serviceName");

		ConnectionConfiguration config = new ConnectionConfiguration(host,port,serviceName);
		config.setDebuggerEnabled(env.getProperty("xmpp.debug",Boolean.class));
		return config;
	}

	@Bean
	public XMPPConnection newConnection(ConnectionConfiguration configuration,CommandManager commandManager){
		XMPPConnection connection = new XMPPTCPConnection(configuration);
		connection.setPacketReplyTimeout(5 * 1000);
		connection.addPacketListener(commandManager.getListener(),commandManager.getFilter());
		return connection;
	}



	@Bean(name="jabbot")
	public Jabbot newInstance(XMPPConnection connection){
		Jabbot jabbot = new Jabbot(connection);
		jabbot.setUsername(env.getProperty("xmpp.credential.username"));
		jabbot.setPassword(env.getProperty("xmpp.credential.password"));
		Map<String,Chatroom> rooms = new HashMap<>();
		for (Chatroom room : (List<Chatroom>)chatroomList) {
			rooms.put(room.getRoomName(),room);
		}
		jabbot.setChatroomList(rooms);

		return jabbot;
	}
}
