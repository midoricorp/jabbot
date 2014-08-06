package org.wanna.jabbot.extensions.jira;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wanna.jabbot.extensions.jira.binding.Issue;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-07-02
 */
public class IssueParserTest {
	private ObjectMapper mapper;
	private InputStream inputStream;

	@Before
	public void before(){
		mapper = new ObjectMapper(); // can reuse, share globally
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@After
	public void after() throws IOException {
		if(inputStream != null){
			inputStream.close();
		}
	}

	@Test
	public void parseFromStream() throws IOException {
		inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("mky-1.json");
		Issue issue = mapper.readValue(inputStream, Issue.class);

		Assert.assertThat(issue.getKey(), is("MKY-1"));
		Assert.assertThat(issue.getFields().getSummary(),is("First Test Issue"));
		Assert.assertThat(issue.getFields().getAssignee().getDisplayName(), is("Administrator"));
		Assert.assertThat(issue.getFields().getReporter().getDisplayName(),is("Administrator"));
		Assert.assertThat(issue.getFields().getStatus().getName(),is("Open"));
		Assert.assertThat(issue.getFields().getResolution(),nullValue());
	}

	@Test
	public void parseNotFound() throws IOException{
		inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("issue-not-found.json");
		Issue issue = mapper.readValue(inputStream,Issue.class);
		Assert.assertThat(issue,notNullValue());
		Assert.assertThat(issue.getKey(),nullValue());
	}
}
