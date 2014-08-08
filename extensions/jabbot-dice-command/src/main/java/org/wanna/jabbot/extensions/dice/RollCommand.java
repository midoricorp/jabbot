package org.wanna.jabbot.extensions.dice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.util.Random;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-06
 */
public class RollCommand extends AbstractCommand{
	private Logger logger = LoggerFactory.getLogger(RollCommand.class);

	private final Random randomizer = new Random(System.currentTimeMillis());

	public RollCommand(String commandName) {
		super(commandName);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String[] args = getParsedCommand().getArgs();

		//Set default values
		int amount = 1;
		int value = 6;

		try {
			if(args != null && args.length > 0) {
				if (args.length > 1) {
					amount = Integer.parseInt(args[0]);
					value = Integer.parseInt(args[1]);
				} else {
					value = Integer.parseInt(args[0]);
				}
			}
		}catch(NumberFormatException e){
			logger.debug("invalid parameter",e);
			chatroom.sendMessage("Invalid parameters");
			return;
		}

		if(value <= 0 ){
			logger.debug("illegal dice value: {}",value);
			chatroom.sendMessage("invalid dice value: " + value);
			return;
		}

		if(amount <= 0){
			chatroom.sendMessage("You need to roll at least 1 dice");
			return;

		}

		if(amount > 5 ){
			chatroom.sendMessage("You cannot roll more than 5 dices");
			return;
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
		chatroom.sendMessage(response);
	}

	private int pick(int max){
		int result = randomizer.nextInt(max);
		return result+1;
	}
}
