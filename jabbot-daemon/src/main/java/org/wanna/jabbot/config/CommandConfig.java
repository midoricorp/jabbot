package org.wanna.jabbot.config;

import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.wanna.jabbot.Chatroom;
import org.wanna.jabbot.command.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */
@Configuration
@ImportResource("classpath:chatrooms.xml")
@PropertySource("classpath:jabbot.properties")
public class CommandConfig {
	@Autowired @Qualifier("chatrooms")
	ArrayList chatroomList;
	@Autowired
	Environment env;

	@Bean
	public CommandManager newCommandManager(){
		return new CommandManager(
				newCommandParser(),
				newCommandFactory(),
				newMucCommandListener(),
				newMucCommandPacketFilter()
		);
	}

	@Bean(name="commandFactory")
	public CommandFactory newCommandFactory(){
		return new JabbotCommandFactory();
	}

	@Bean
	public CommandParser newCommandParser(){
		DefaultCommandParser parser = new DefaultCommandParser();
		parser.setCommandPrefix(env.getProperty("bot.command.prefix"));
		return parser;
	}

	@Bean
	public MucCommandListener newMucCommandListener(){
		Map<String,Chatroom> rooms = new HashMap<>();
		for (Chatroom room : (List<Chatroom>)chatroomList) {
			rooms.put(room.getRoomName(),room);
		}

		MucCommandListener listener = new MucCommandListener(rooms);
		listener.setCommandFactory((JabbotCommandFactory)newCommandFactory());
		listener.setCommandParser(newCommandParser());
		return listener;
	}

	@Bean
	public PacketFilter newMucCommandPacketFilter(){
		PacketFilter filter = new AndFilter(
				new MessageTypeFilter(Message.Type.groupchat),
				new PacketFilter() {
					@Override
					public boolean accept(Packet packet) {
						return packet instanceof Message && ((Message) packet).getBody().startsWith(env.getProperty("bot.command.prefix"));
					}
				}
		);
		return filter;
	}


}
