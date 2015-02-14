package org.wanna.jabbot.extensions.propaganda;

import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

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
	public void process(MucHolder chatroom, MessageWrapper message) {
		String[] args = getParsedCommand().getArgs();
		int i = Math.abs(rand.nextInt())%quotes.size();
		String response = quotes.get(i);

		for (String key : replace.keySet()) {
			response = response.replace(key, replace.get(key));
		}

		if(args != null && args.length > 0){
		}
		else{
		}
		chatroom.sendMessage(response);
	}

	@Override
	public void configure(Map<String, Object> configuration) {
		if (configuration == null ) return;

		for (String key : configuration.keySet()) {
			replace.put(key, String.valueOf(configuration.get(key)));
		}

	}
}
