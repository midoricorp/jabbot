package org.wanna.jabbot.command.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsearle <tsearle>
 * @since 2015-03-1
 */
public class NullArgParser implements ArgsParser {
	@Override
	public List<String> parse(String argLine) {
		List<String> args = new ArrayList<>();
		args.add(argLine);
		return args;
	}
}
