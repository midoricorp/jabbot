package org.wanna.jabbot.binding.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.config.RoomConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.CommandNotFoundException;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.parser.CommandParser;
import org.wanna.jabbot.command.parser.ParsedCommand;
import java.io.*;


import java.util.StringTokenizer;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class CliRoom extends AbstractRoom<Object> implements Runnable {
	private final static Logger logger = LoggerFactory.getLogger(CliRoom.class);
        private CommandFactory commandFactory;
	private CommandParser commandParser;
	private RoomConfiguration configuration;


	public CliRoom(CliConnection connection, CommandFactory commandFactory, CommandParser commandParser) {
		super(connection);
		this.commandFactory = commandFactory;
		this.commandParser = commandParser;
	}

	@Override
	public void run()
	{
		System.out.println("CLI Room started");
		BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String line=buffer.readLine();
				logger.debug("received {}",line);

				if(line == null) {
					logger.error("Got a null, probably no console, dying now");
					return;
				}
				if(line.startsWith(commandParser.getCommandPrefix())){
					ParsedCommand parsedCommand = commandParser.parse(line);
					try {
						Command command = commandFactory.create(parsedCommand);
						MessageWrapper wrapper = new MessageWrapper(line);
						wrapper.setSender("cli");
						wrapper.setBody(line);
						command.process(this,wrapper);
					} catch (CommandNotFoundException e) {
						logger.error("erorr instantating command",e);
					}

				}
			} catch (IOException e) {
				logger.error("IO Error reading sdtin, dying");
				return;
			}
		}
	}

	@Override
	public RoomConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public boolean sendMessage(String message) {
		System.out.println(message);
		return true;
	}

	@Override
	public boolean join(final RoomConfiguration configuration) {
		this.configuration = configuration;
		new Thread(this).start();
		return true;
	}

	@Override
	public String getNickname() {
		return configuration.getNickname();
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}
}
