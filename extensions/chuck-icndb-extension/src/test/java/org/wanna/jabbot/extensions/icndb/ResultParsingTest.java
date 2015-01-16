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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wanna.jabbot.extensions.icndb.binding.Entry;
import org.wanna.jabbot.extensions.icndb.binding.Result;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-14
 */
public class ResultParsingTest {
	private ObjectMapper mapper;
	private final String json = "{ \"type\": \"success\", \"value\": { \"id\": 4, \"joke\": \"If you ask Chuck Norris what time it is, he always answers &quot;Two seconds till&quot;. After you ask &quot;Two seconds to what?&quot;, he roundhouse kicks you in the face.\", \"categories\": [] } }";

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
		Assert.assertThat(entry.getId(), is(4l));
		Assert.assertThat(StringEscapeUtils.unescapeHtml4(entry.getJoke()),is("If you ask Chuck Norris what time it is, he always answers \"Two seconds till\". After you ask \"Two seconds to what?\", he roundhouse kicks you in the face."));
	}
}
