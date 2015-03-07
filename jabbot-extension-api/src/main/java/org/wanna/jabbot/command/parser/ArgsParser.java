package org.wanna.jabbot.command.parser;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-18
 */
public interface ArgsParser {
	/**
	 * Take the raw argument line and transform it into a list of parameters.
	 *
	 * @param argLine The raw argument line. Usually the command message body
	 *
	 * @return List of parsed arguments
	 */
	List<String> parse(String argLine);
}
