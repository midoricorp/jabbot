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
import org.wanna.jabbot.command.MessageWrapper;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.extensions.AbstractCommand;
import org.wanna.jabbot.extensions.jira.binding.Issue;

import java.io.IOException;
import java.util.Map;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-20
 */
public class IssueViewer extends AbstractCommand implements Configurable{
	final Logger logger = LoggerFactory.getLogger(IssueViewer.class);
	private String baseUrl;
	private String username;
	private String password;
	private ObjectMapper mapper;
	private UsernamePasswordCredentials credentials;
	private final DefaultHttpClient httpclient = new DefaultHttpClient();


	public IssueViewer(String commandName) {
		super(commandName);
		mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Override
	public void configure(Map<String, Object> configuration) {
		this.baseUrl = (String)configuration.get("url");
		this.username = (String)configuration.get("username");
		this.password = (String)configuration.get("password");
	}

	@Override
	public void process(MucHolder chatroom, MessageWrapper message) {
		String[] args = super.getParsedCommand().getArgs();
		if( args == null || args.length < 1 ){
			chatroom.sendMessage("invalid parameter");
			return;
		}

		String key = args[0];

		try {
			String response = query("/rest/api/latest/issue/"+key);
			logger.trace("response: {}", response);

			Issue issue = mapper.readValue(response,Issue.class);
			StringBuilder sb = new StringBuilder();
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
			}
			chatroom.sendMessage(sb.toString());
		} catch (IOException e) {
			logger.error("error querying",e);
		}
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

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
