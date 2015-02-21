package org.wanna.jabbot.binding.irc;

import com.ircclouds.irc.api.Callback;
import com.ircclouds.irc.api.IRCApi;
import com.ircclouds.irc.api.domain.IRCChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractRoom;
import org.wanna.jabbot.binding.config.RoomConfiguration;

import java.util.StringTokenizer;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class IrcRoom extends AbstractRoom<IrcBinding> {
	private final static Logger logger = LoggerFactory.getLogger(IrcRoom.class);
	private RoomConfiguration configuration;

	public IrcRoom(IrcBinding connection) {
		super(connection);
	}

	@Override
	public RoomConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public boolean sendMessage(String message) {
		IRCApi ircApi = connection.getWrappedConnection();
		StringTokenizer tokenizer  = new StringTokenizer(message,"\n");
		while (tokenizer.hasMoreElements()){
			ircApi.message("#"+configuration.getName(),tokenizer.nextToken());
		}

		return true;
	}

	@Override
	public boolean join(final RoomConfiguration configuration) {
		this.configuration = configuration;
		IRCApi ircApi = connection.getWrappedConnection();
		ircApi.joinChannel(configuration.getName(), new Callback<IRCChannel>() {
			@Override
			public void onSuccess(IRCChannel ircChannel) {
				logger.info("[IRC] joining room {}",configuration.getName());
			}

			@Override
			public void onFailure(Exception e) {

			}
		});
		return true;
	}

	@Override
	public String getRoomName() {
		return configuration.getName();
	}
}
