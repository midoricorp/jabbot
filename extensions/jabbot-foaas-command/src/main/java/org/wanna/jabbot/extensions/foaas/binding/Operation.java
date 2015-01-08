package org.wanna.jabbot.extensions.foaas.binding;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-07
 */
public class Operation {
	final Logger logger = LoggerFactory.getLogger(Operation.class);

	private List<Field> fields;
	private String name,url;

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Build the final url populated with fields value
	 *
	 * @return populated url
	 */
	public String getPopulatedUrl(){
		for (Field field : fields) {
			url = url.replace(":"+field.getField(),field.getValue());
		}
		return url;
	}

	public String execute(){
		final String baseUrl = "http://foaas.com";
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(baseUrl +getPopulatedUrl());
		httpGet.setHeader("Accept","text/plain");
		logger.debug("foaas url: {}",httpGet.getURI().toString());

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
