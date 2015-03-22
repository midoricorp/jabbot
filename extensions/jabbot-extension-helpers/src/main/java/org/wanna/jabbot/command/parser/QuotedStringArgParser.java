package org.wanna.jabbot.command.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tsearle <tsearle>
 * @since 2015-02-18
 */
public class QuotedStringArgParser implements ArgsParser {
	@Override
	public List<String> parse(String argLine) {
		List<String> args = new ArrayList<>();
		boolean inQuote = false;
		boolean inToken = false;
		int start = 0;
		int end = 0;
		for (;end < argLine.length();end++) {
			if (inQuote) {
				if (argLine.charAt(end) == '\\') {
					if (end+1 < argLine.length()
						&& argLine.charAt(end+1) == '"') {
						// escaped " encountered skip it
						end++;
						continue;
					}
				}
				// if in a quoted string, token ends with endquote
				if (argLine.charAt(end) == '"') {
					String token = argLine.substring(start+1,end);
					// unescape at this point
					token = token.replace("\\\"", "\"");
					args.add(token);
					inQuote = false;
					inToken = false;
				}
			} else if (!inToken) {
				// skip wite space until we hit the token start
				if(argLine.charAt(end) != ' '
						&& argLine.charAt(end) != '\t'
						&& argLine.charAt(end) != '\r'
						&& argLine.charAt(end) != '\n')
				{
					inToken = true;
					start = end;

					if (argLine.charAt(end) == '"') {
						// woo this token is quoted
						// special rules apply!
						inQuote = true;
					}
				}
			} else {
				if(argLine.charAt(end) == ' '
						|| argLine.charAt(end) == '\t'
						|| argLine.charAt(end) == '\r'
						|| argLine.charAt(end) == '\n')
				{
					inToken = false;
					String token = argLine.substring(start,end);
					args.add(token);
				}
			}
		}

		if (inToken) {
			// hit the end and it isn't whitespace! let's record the token
			inToken = false;
			String token = argLine.substring(inQuote?start+1:start,end);
			if (inQuote) {
				// unescape at this point
				token = token.replace("\\\"", "\"");
				inQuote = false;
			}
			args.add(token);
		}

		return args;
	}
}
