package org.wanna.jabbot.extensions.quote;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.AbstractCommandAdapter;
import org.wanna.jabbot.extensions.quote.binding.Result;

import java.io.IOException;

/**
 * Quote command based on iheartquotes.com
 * Quotes will be randomly picked via their REST API
 *
 * See <a href="http://www.iheartquotes.com">http://www.iheartquotes.com</a>
 *
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-31
 */
public class QuoteCommand extends AbstractCommandAdapter{
	final String baseUrl = "http://www.iheartquotes.com/api/v1/random?format=json";
	final Logger logger = LoggerFactory.getLogger(QuoteCommand.class);
	final ObjectMapper mapper = new ObjectMapper();

	public QuoteCommand(CommandConfig configuration) {
		super(configuration);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String response = query(null);
		if(response != null){
			try {
				Result result = mapper.readValue(response,Result.class);
				chatroom.sendMessage(result.getQuote());
			} catch (IOException e) {
				logger.error("error parsing json string {}",response,e);
			}
		}
	}

	private String query(String option){
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		String url = baseUrl;
		if(option != null ){
			url += option;
		}
		logger.debug("querying {}",url);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept","application/json");

		try
		{
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				return EntityUtils.toString(entity, HTTP.UTF_8);
			}
		} catch (IOException e) {
			logger.error("error querying icndb",e);
		}

		return null;
	}
}
