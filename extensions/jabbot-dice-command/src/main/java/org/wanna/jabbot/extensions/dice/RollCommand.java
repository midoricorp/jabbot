package org.wanna.jabbot.extensions.dice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;
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
	public Message process(Message message) {
		List<String> args =  getArgsParser().parse(message.getBody());
		DefaultMessage result = new DefaultMessage();
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
			result.setBody("Invalid parameters");
			return result;
		}

		if(value <= 0 ){
			logger.debug("illegal dice value: {}",value);
			result.setBody("invalid dice value: " + value);
			return result;
		}

		if(amount <= 0){
			result.setBody("You need to roll at least 1 dice");
			return result;

		}

		if(amount > 5 ){
			result.setBody("You cannot roll more than 5 dices");
			return result;
		}

		String resultString = "";
		int total = 0;

		for (int i=0;i<amount;i++){
			int rand = pick(value);
			total += rand;
			resultString+= " "+rand;
			logger.debug("roll {} for a new total of {},\ndetails: {}",rand,total,resultString);

		}

		String player = message.getSender();
		String response = String.format("%s rolled %s dice of %s for a total of %s\n:details: %s",player,amount,value,total,resultString);
		result.setBody(response);
		return result;
	}

	private int pick(int max){
		int result = randomizer.nextInt(max);
		return result+1;
	}
}
