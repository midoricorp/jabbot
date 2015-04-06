package org.wanna.jabbot.extensions.jira;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.CommandMessage;
import org.wanna.jabbot.command.DefaultCommandMessage;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.extensions.jira.binding.Issue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-20
 */
public class IssueViewer extends AbstractCommandAdapter {
	final Logger logger = LoggerFactory.getLogger(IssueViewer.class);
	private String baseUrl;
	private String username;
	private String password;
	private ObjectMapper mapper;
	private UsernamePasswordCredentials credentials;
	private final DefaultHttpClient httpclient = new DefaultHttpClient();

	public IssueViewer(CommandConfig configuration) {
		super(configuration);
		mapper = new ObjectMapper(); //TODO can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Override
	public void configure(Map<String, Object> configuration) {
		this.baseUrl = (String)configuration.get("url");
		this.username = (String)configuration.get("username");
		this.password = (String)configuration.get("password");
	}

	@Override
	public CommandMessage process(CommandMessage message) {
		List<String> args =  getArgsParser().parse(message.getBody());
		DefaultCommandMessage result = new DefaultCommandMessage();
		if( args == null || args.isEmpty() ){
			result.setBody("invalid parameter");
			return result;
		}

		StringBuilder sb = new StringBuilder();
		for (String key : args) {
			try {
				String response = query("/rest/api/latest/issue/"+key);
				logger.trace("response: {}", response);

				Issue issue = mapper.readValue(response,Issue.class);
				//If issue key is not set, it means issue couldn't be found or accessed
				if(issue == null || issue.getKey() == null ){
					sb.append("No issue found with key ").append(key);
				}else {
					sb.append("[").append(issue.getKey()).append("] ");
					sb.append(issue.getFields().getSummary()).append('\n');
					sb.append("Reporter: ").append(issue.getFields().getReporter().getDisplayName()).append('\n');
					String assignee = (issue.getFields().getAssignee() == null ? "NONE" : issue.getFields().getAssignee().getDisplayName());
					sb.append("Assignee: ").append(assignee).append('\n');
					sb.append("Status: ").append(issue.getFields().getStatus().getName()).append('\n');
					if (issue.getFields().getResolution() != null) {
						sb.append("Resolution: ").append(issue.getFields().getResolution().getName()).append('\n');
					}
					sb.append("URL: ").append(baseUrl).append("/browse/").append(issue.getKey());
					sb.append("\n");
				}
			} catch (IOException e) {
				logger.error("error querying",e);
				return null;
			}
		}
		result.setBody(sb.toString());
		return result;
	}

	private void initCredentials(){
		credentials = new UsernamePasswordCredentials(username,password);
		BasicCredentialsProvider p = new BasicCredentialsProvider();
		p.setCredentials(AuthScope.ANY,credentials);
		httpclient.setCredentialsProvider(p);
	}


	protected String query(String key) throws IOException {
		if(credentials==null){
			initCredentials();
		}
		HttpGet httpGet = new HttpGet(baseUrl+key);

		try
		{
			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null)
			{
				return EntityUtils.toString(entity, HTTP.UTF_8);
			}
		} catch (IOException e) {
			logger.error("error querying jira",e);
		}

		return null;
	}
}
