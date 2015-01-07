package org.wanna.jabbot.extensions.foaas;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AttackCommand extends AbstractCommand{
	final Logger logger = LoggerFactory.getLogger(AttackCommand.class);

	private final String baseUrl = "http://foaas.com";
	private final Random randomizer = new Random();

	public AttackCommand(String commandName) {
		super(commandName);
	}

	private Map<Integer,String[]> getAttacks(){
		Map<Integer,String[]> attacks = new HashMap<>();
		attacks.put(0,new String[]{
				"/this/:from",
				"/that/:from",
				"/everything/:from",
				"/everyone/:from",
				"/pink/:from",
				"/life/:from",
				"/flying/:from",
				"/fascinating/:from"
		});

		attacks.put(1,new String[]{
				"/you/:name/:from",
				"/off/:name/:from",
				"/donut/:name/:from",
				"/shakespeare/:name/:from",
				"/linus/:name/:from",
				"/king/:name/:from",
				"/chainsaw/:name/:from",
				"/:name/:from",
				"/madison/:name/:from"
		});
		return attacks;
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String response;
		if(getParsedCommand().getArgs() != null ){
			String attack = pickAttack(getParsedCommand().getArgs().length);
			String target = null;
			if(getParsedCommand().getArgs().length > 0){
				target = getParsedCommand().getArgs()[0];
				attack = attack.replace(":name",URLEncoder.encode(target));
				if(target.equals(chatroom.getNickname())){
					chatroom.sendMessage("I'm not gonna attack myself, you fool!");
					return;
				}
			}
			String attacker = message.getSender();
			attack = attack.replace(":from",URLEncoder.encode(attacker));
			logger.debug("attack command: {}\nattacker: {}\ntarget: {}",attack,attacker,target);
			try {
				response = query(attack);
			} catch (IOException e) {
				response = "unable to attack target!";
			}
			chatroom.sendMessage(secureResponse(response));
		}
	}

	/**
	 * Make sure one does not use / command from jabber in order to spam someone else
	 * using /say or having the bot acting weird using /me.
	 * by Stripping all the leading / from the response
	 *
	 * @param response the raw response to be returned
	 * @return cleaned response
	 */
	protected String secureResponse(String response){
		response = URLDecoder.decode(response);
		while(response.startsWith("/")){
			response = response.replace("/","");
		}
		return response;
	}

	protected String pickAttack(int argsLength){
		final String[] attacks;
		if(argsLength >= getAttacks().size() ){
			attacks = getAttacks().get(getAttacks().size()-1);
		}else{
			attacks = getAttacks().get(argsLength);
		}
		return attacks[randomizer.nextInt(attacks.length)];
	}

	protected String query(String option) throws IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseUrl+option);
		httpGet.setHeader("Accept","text/plain");

		try
		{
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				return EntityUtils.toString(entity, HTTP.UTF_8);
			}
		}
		catch (ClientProtocolException e) {logger.error("error querying foaas",e);}
		catch (IOException e) {logger.error("error querying foaas",e);}

		return null;
	}
}
