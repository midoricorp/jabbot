package org.wanna.jabbot.command;

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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Random;

public class AttackCommand extends AbstractCommand{
	final Logger logger = LoggerFactory.getLogger(AttackCommand.class);

	private final String baseUrl = "http://foaas.com";
	private final Random randomizer = new Random();

	private final String[] attacks = new String[]{
			"/you/:name/:from",
			"/off/:name/:from",
			"/donut/:name/:from",
			"/shakespeare/:name/:from",
			"/linus/:name/:from",
			"/king/:name/:from",
			"/chainsaw/:name/:from"
	};

	@Override
	public String getCommandName() {
		return "attack";
	}

	@Override
	public void process(MucHolder chatroom, Message message) throws XMPPException, SmackException.NotConnectedException {
		String response;
		if(getParsedCommand().getArgs() != null && getParsedCommand().getArgs().length > 0){
			String attack = pickAttack();
			String target = getParsedCommand().getArgs()[0];
			String attacker = StringUtils.parseResource(message.getFrom());
			attack = attack.replace(":from",attacker);
			attack = attack.replace(":name",secureTarget(target));
			logger.debug("attack command: {}\nattacker: {}\ntarget: {}",attack,attacker,target);
			try {
				response = query(attack);
			} catch (IOException e) {
				response = "unable to fuck you! (^_^)";
			}
			chatroom.getMuc().sendMessage(response);
		}
	}

	@Override
	public void process(Message message) {

	}

	protected String secureTarget(String target){
		return URLEncoder.encode(target);
	}

	protected String pickAttack(){
		return attacks[randomizer.nextInt(attacks.length)];
	}

	protected String query(String option) throws IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(baseUrl+option);

		try
		{
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				return EntityUtils.toString(entity, HTTP.UTF_8);
			}
		}
		catch (ClientProtocolException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}

		return null;
	}
}
