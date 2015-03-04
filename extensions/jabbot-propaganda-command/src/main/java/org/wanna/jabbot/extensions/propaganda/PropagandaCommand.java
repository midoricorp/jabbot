package org.wanna.jabbot.extensions.propaganda;

import org.wanna.jabbot.command.CommandResult;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;

import java.io.InputStream;
import java.util.*;

/**
 * @author tsearle 
 * @since 2015-02-14
 */
public class PropagandaCommand extends AbstractCommandAdapter {
	private ArrayList<String> quotes = null;
	private HashMap<String,String> replace = null;
	private Random rand = null;

	public PropagandaCommand(CommandConfig configuration) {
		super(configuration);
		rand = new Random();
		quotes = new ArrayList<String>();
		replace = new HashMap<String,String>();
		//Get file from resources folder
		InputStream in = getClass().getResourceAsStream("/slogans.txt");
		Scanner scanner = new Scanner(in);
	 
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			quotes.add(line);
		}
 
		scanner.close();
	 
		
	}

	@Override
	public String getHelpMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(getCommandName());
		sb.append(" [filter [= replacement]]*\n");
		sb.append("Returns a quote filterd with the optional keywords\n");
		sb.append("Current Substitution Map:\n");

		for (String key : replace.keySet()) {
			sb.append(key);
			sb.append(" = ");
			sb.append(replace.get(key));
			sb.append("\n");
		}

		return sb.toString();
	}

	@Override
	public CommandResult process(MessageWrapper message) {
		String[] args =  message.getArgs().toArray(new String[message.getArgs().size()]);
		ArrayList<String> quote_list = null;
		ArrayList<String> filter_list = new ArrayList<String>();
		HashMap<String,String> tmp_replace = new HashMap<String,String>();


		if (args.length > 0){
			for (int i = 0; i < args.length; i++) {
				filter_list.add(args[i]);

				// do we have a substitution rule?
				if (i+2 < args.length
						&& args[i+1].equals("=")) {
					tmp_replace.put(args[i], args[i+2]);
					i += 2;
				}
			}

			quote_list = new ArrayList<String>();
			for (String quote : quotes) {
				boolean found = true;
				for (String arg : filter_list) {
					if (!quote.contains(arg)) {
						found = false;
						break;
					}
				}

				if (found) {
					quote_list.add(quote);
				}
			}

			if (quote_list.size() == 0) {
				quote_list.add("No matching propaganda found");
			}
		}
		else {
			quote_list = quotes;
		}

		int i = Math.abs(rand.nextInt())%quote_list.size();
		String response = quote_list.get(i);

		//first do the override substitutions
		for (String key : tmp_replace.keySet()) {
			response = response.replace(key, tmp_replace.get(key));
		}

		//then the default ones
		for (String key : replace.keySet()) {
			// if we override this substitution, skip it
			if (tmp_replace.containsKey(key)) {
				continue;
			}

			response = response.replace(key, replace.get(key));
		}
		CommandResult result = new CommandResult();
		result.setText(response);
		return result;
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;

		for (String key : configuration.keySet()) {
			replace.put(key, String.valueOf(configuration.get(key)));
		}

	}
}
