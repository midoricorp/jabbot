package org.wanna.jabbot.extensions.foaas;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-05-31
 */

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.messaging.Message;
import org.wanna.jabbot.command.messaging.DefaultMessage;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.foaas.binding.Field;
import org.wanna.jabbot.extensions.foaas.binding.Operation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public class AttackCommand extends AbstractCommandAdapter {
	final Logger logger = LoggerFactory.getLogger(AttackCommand.class);

	private final Random randomizer = new Random();
	private Map<Integer,List<Operation>> operationsMap = new HashMap<>();

	public AttackCommand(CommandConfig configuration) {
		super(configuration);
		initializeOperations();
	}

	@Override
	public Message process(Message message) {
		List<String> args = getArgsParser().parse(message.getBody());
		String response;
		if(args != null ){
			//Add +1 to args lenght as we'll always have a "from" arg from Sender
			int length = args.size()+1;
			if(length > 2){
				length = 2;
			}

			Operation operation = pickOperation(length);
			String from = message.getSender();

			try {
				String url = buildUrl(operation,from,args);
				response = query(url);
				DefaultMessage result = new DefaultMessage();
				result.setBody(secureResponse(response));
				return result;
			} catch (IOException e) {
				logger.error("error while querying foaas",e);

			}
		}
		return null;
	}

	private String buildUrl(Operation operation,String from, List<String> args){
		String url = operation.getUrl();
		int i = 0;
		for (Field field : operation.getFields()) {
			String value;
			if(field.getField().equalsIgnoreCase("from")){
				value = from;
				try {
					url = url.replace(":"+field.getField(),URLEncoder.encode(from,"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error("unable to encode from",value,e);
				}
			}else{
				value = args.get(i);
				i++;
			}
			try {
				url = url.replace(":"+field.getField(),URLEncoder.encode(value,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error("unable to encode {}",value,e);
			}
		}
		return url;
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
		response = URLDecoder.decode(response,"UTF-8");
		while(response.startsWith("/")){
			response = response.replaceFirst("/", "");
		}
		return response;
	}

	/**
	 * Picks a list of operations based on parameter count
	 * then picks a random operation out of the list
	 *
	 * @param argsLength amount of available parameters
	 *
	 * @return Random operation based on argsLength
	 */
	private Operation pickOperation(int argsLength){
		if(argsLength > operationsMap.size()){
			argsLength = operationsMap.size();
		}

		final List<Operation> attacks;
		attacks = operationsMap.get(argsLength);
		return attacks.get(randomizer.nextInt(attacks.size()));
	}

	/**
	 * Initialize an operations map using the http://foaas.herokuapp.com/operations
	 * If the operation requires more than 2 fields discard it.
	 * discarding should be done until at least proper args delimiter is supported
	 *
	 * supported fields are:
	 * From: represents the user which triggered the command
	 * Name: represents the first argument passed to the command
	 */
	private void initializeOperations(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String json;
		List<Operation> operations;
		try {
			json = query("/operations");
			TypeFactory typeFactory = mapper.getTypeFactory();
			CollectionType collectionType = typeFactory.constructCollectionType(
					List.class, Operation.class);
			operations = mapper.readValue(json, collectionType);
			for (Operation operation : operations) {
				//Only allow 2 fields commands for now (from & name)
				if(operation.getFields().size() <= 2) {
					if (!operationsMap.containsKey(operation.getFields().size())) {
						ArrayList<Operation> array = new ArrayList<>();
						array.add(operation);
						operationsMap.put(operation.getFields().size(), array);
					} else {
						operationsMap.get(operation.getFields().size()).add(operation);
					}
				}
			}
		} catch (IOException e) {
			logger.error("error initializing operation map",e);
		}

	}

	private String query(String option) throws IOException {
		final DefaultHttpClient httpclient = new DefaultHttpClient();
		final String baseUrl = "http://foaas.com";
		HttpGet httpGet = new HttpGet(baseUrl +option);
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
			logger.error("error querying foaas",e);
		}

		return null;
	}
}
