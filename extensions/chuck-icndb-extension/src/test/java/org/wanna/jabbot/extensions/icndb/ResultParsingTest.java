package org.wanna.jabbot.extensions.icndb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wanna.jabbot.extensions.icndb.binding.Entry;
import org.wanna.jabbot.extensions.icndb.binding.Result;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-14
 */
public class ResultParsingTest {
	private ObjectMapper mapper;
	private final String json = "{ \"type\": \"success\", \"value\": { \"id\": 268, \"joke\": \"Time waits for no man. Unless that man is Chuck Norris.\" } }\n";

	@Before
	public void before() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Test
	public void parse() throws IOException {
		Result result = mapper.readValue(json,Result.class);
		Assert.assertThat(result.getType(), is("success"));
		Entry entry = result.getValue();
		Assert.assertThat(entry.getId(), is(268l));
		Assert.assertThat(entry.getJoke(),is("Time waits for no man. Unless that man is Chuck Norris."));
	}
}
