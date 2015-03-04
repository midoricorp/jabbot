package org.wanna.jabbot.binding.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.AbstractBinding;
import org.wanna.jabbot.binding.Room;
import org.wanna.jabbot.binding.config.BindingConfiguration;
import org.wanna.jabbot.binding.config.RoomConfiguration;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-08-14
 */
public class CliBinding extends AbstractBinding<Object> {
	private final Logger logger = LoggerFactory.getLogger(CliBinding.class);
	private Room room;

	public CliBinding(BindingConfiguration configuration) {
		super(configuration);
	}

	@Override
	public boolean connect(BindingConfiguration configuration) {
		return true;
	}

	@Override
	public Room joinRoom(RoomConfiguration configuration) {
		logger.debug("Joining room " + configuration.getName());
		room = new CliRoom(this,listeners);
		room.join(configuration);
		return room;
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public Room getRoom(String roomName) {
		return room;
	}
}
