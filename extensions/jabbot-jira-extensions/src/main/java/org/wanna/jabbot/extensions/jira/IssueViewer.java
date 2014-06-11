package org.wanna.jabbot.extensions.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.command.MucHolder;
import org.wanna.jabbot.extensions.AbstractCommand;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-06-04
 */
public class IssueViewer extends AbstractCommand{
	final Logger logger = LoggerFactory.getLogger(IssueViewer.class);
	private String username, password;
	private String jiraUrl;

	@Override
	public String getCommandName() {
		return "jira";
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setJiraUrl(String jiraUrl) {
		this.jiraUrl = jiraUrl;
	}

	@Override
	public void process(MucHolder chatroom, Message message) throws XMPPException, SmackException.NotConnectedException {
		String key = getParsedCommand().getArgs()[0];
		try {
			Issue issue = retrieveIssue(key);
			StringBuilder sb = new StringBuilder();
			if(issue != null ){
				sb.append("[").append(issue.getKey()).append("] ");
				sb.append(issue.getSummary()).append("\n");
				sb.append("Reporter: ").append((issue.getReporter() != null ? issue.getReporter().getDisplayName() : "UNKNOWN")).append("\n");
				sb.append("Assignee: ").append(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : "NONE").append("\n");
				sb.append("Status: ").append(issue.getStatus().getName()).append("\n");
				if(issue.getResolution() != null ){
					sb.append("Resolution: ").append(issue.getResolution().getName());
				}
			}else{
				sb.append("unable to find issue with key '").append(key).append("'");
			}

			chatroom.getMuc().sendMessage(sb.toString());
		} catch (Exception e) {
			logger.error("error while retrieving issue",e);
		}
	}

	private com.atlassian.jira.rest.client.domain.Issue retrieveIssue(String key) throws URISyntaxException, InterruptedException {
		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
		final URI jiraServerUri = new URI(jiraUrl);
		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
		final NullProgressMonitor pm = new NullProgressMonitor();
		try{
			final com.atlassian.jira.rest.client.domain.Issue issue = restClient.getIssueClient().getIssue(key, pm);
			logger.trace(issue.toString());
			return issue;

		}catch(RestClientException e){
			logger.error("rest exception ",e);
			return null;
		}

	}

}
