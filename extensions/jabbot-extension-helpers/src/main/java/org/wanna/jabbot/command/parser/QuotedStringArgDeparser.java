package org.wanna.jabbot.command.parser;

import java.util.List;

/**
 * pretty much all commands use Quoted String for arg parsing
 * if you want to pass args to such a command use this class to turn
 * your param list back into a string, this class needs to be made
 * more robust as the corresponding QuotedStringArgParser Is
 *
 * @author tsearle
 */
public class QuotedStringArgDeparser {

	public static String deparse(List<String> args) {
		StringBuffer sb = new StringBuffer();

		for (String arg : args) {
			sb.append("\"");
			// escape "
			arg = arg.replace("\"", "\\\"");
			sb.append(arg);
			sb.append("\" ");
		}
		return sb.toString();
	}
}
