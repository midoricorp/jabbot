package org.wanna.jabbot.extensions.dice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.messaging.DefaultMessageContent;
import org.wanna.jabbot.binding.messaging.MessageContent;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;
import org.wanna.jabbot.command.config.CommandConfig;

import java.util.List;
import java.util.Random;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-06
 */
public class RollCommand extends AbstractCommandAdapter {
	private Logger logger = LoggerFactory.getLogger(RollCommand.class);

	private final Random randomizer = new Random(System.currentTimeMillis());

	public RollCommand(CommandConfig configuration) {
		super(configuration);
	}

	@Override
	public MessageContent process(CommandMessage message) {
		List<String> args =  getArgsParser().parse(message.getArgsLine());
		//Set default values
		int amount = 1;
		int value = 6;

		try {
			if(args != null && args.size() > 0) {
				if (args.size() > 1) {
					amount = Integer.parseInt(args.get(0));
					value = Integer.parseInt(args.get(1));
				} else {
					value = Integer.parseInt(args.get(0));
				}
			}
		}catch(NumberFormatException e){
			logger.debug("invalid parameter",e);
			return new DefaultMessageContent("Invalid parameters");
		}

		if(value <= 0 ){
			logger.debug("illegal dice value: {}",value);
			return new DefaultMessageContent("invalid dice value: " + value);
		}

		if(amount <= 0){
			return new DefaultMessageContent("You need to roll at least 1 dice");

		}

		if(amount > 5 ){
			return new DefaultMessageContent("You cannot roll more than 5 dices");
		}

		String resultString = "";
		int total = 0;

		for (int i=0;i<amount;i++){
			int rand = pick(value);
			total += rand;
			resultString+= " "+rand;
			logger.debug("roll {} for a new total of {},\ndetails: {}",rand,total,resultString);

		}

		String player = message.getSender().getName();
		String response = String.format("%s rolled %s dice of %s for a total of %s\n:details: %s",player,amount,value,total,resultString);
		return new DefaultMessageContent(response);
	}

	private int pick(int max){
		int result = randomizer.nextInt(max);
		return result+1;
	}
}
