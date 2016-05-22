package org.wanna.jabbot.command.messaging;

import org.wanna.jabbot.binding.messaging.Resource;

/**
 * CommandMessage are lightweight messages which are passed onto a {@link org.wanna.jabbot.command.Command} in order to
 * be processed.
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-19
 */
public interface CommandMessage {
	/**
	 * The raw text argument line which as been passed to the command.
	 * This usually is a copy of MessageContent TEXT body minus the command name itself
	 * For example if MessageContent is
	 * <code>
	 * #command arg1 arg2
	 * </code>
	 * argsLine would then be a String containing arg1 arg2
	 * Removing the command name from the TEXT body is usually the responsibility of
	 * {@link org.wanna.jabbot.command.parser.CommandParser}
	 *
	 * @return String
	 */
    String getArgsLine();

	/**
	 * Resource which initiated the command invocation.
	 *
	 * @return resource
	 */
    Resource getSender();
}
