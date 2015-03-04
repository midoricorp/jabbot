package org.wanna.jabbot.extensions.quote.binding;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-31
 */
public class Result {
	private String quote;
	private List<String> tags;
	private String jsonClass;

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getJsonClass() {
		return jsonClass;
	}

	public void setJsonClass(String jsonClass) {
		this.jsonClass = jsonClass;
	}
}
