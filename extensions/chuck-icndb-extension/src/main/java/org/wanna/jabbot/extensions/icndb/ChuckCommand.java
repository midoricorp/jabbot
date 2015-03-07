package org.wanna.jabbot.extensions.icndb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.CommandMessage;
import org.wanna.jabbot.command.DefaultCommandMessage;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.icndb.binding.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-14
 */
public class ChuckCommand extends AbstractCommandAdapter {
	final Logger logger = LoggerFactory.getLogger(ChuckCommand.class);
	final ObjectMapper mapper = new ObjectMapper();

	public ChuckCommand(CommandConfig configuration) {
		super(configuration);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public DefaultCommandMessage process(CommandMessage message) {
		List<String> args = getArgsParser().parse(message.getBody());
		String options = null;
		if(args != null && args.size() > 0){
			try {
				options = "&firstName="+ URLEncoder.encode(args.get(0),"UTF-8");
				if(args.size() > 1){
					options+="&lastName="+ URLEncoder.encode(args.get(1),"UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("An error occured while encoding param {}",message.getBody(),e);
			}
		}
		try {
			String response = query(options);
			Result parsed = mapper.readValue(response,Result.class);
			if(parsed.getType().equalsIgnoreCase("success")){
				String joke = StringEscapeUtils.unescapeHtml4(parsed.getValue().getJoke());
				DefaultCommandMessage result = new DefaultCommandMessage();
				result.setBody(joke);
				return result;
			}
		} catch (IOException e) {
			logger.error("error querying icndb",e);
		}
		return null;
	}

	/**
	 * Make sure one does not use / command from jabber in order to spam someone else
	 * using /say or having the bot acting weird using /me.
	 * by Stripping all the leading / from the response
	 *
	 * @param response the raw response to be returned
	 * @return cleaned response
	 */
	private String secureResponse(String response) throws UnsupportedEncodingException {
		logger.debug("securing response {}",response);
		response = URLDecoder.decode(response, "UTF-8");
		while(response.startsWith("/")){
			response = response.replace("/","");
		}
		return response;
	}

	private String query(String option) throws IOException {
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		String url = "http://api.icndb.com/jokes/random?escape=html?exclude=[explicit]";
		if(option != null ){
			url += option;
		}
		logger.debug("querying {}",url);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept","text/plain");

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
