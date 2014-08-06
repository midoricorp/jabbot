package org.wanna.jabbot.extensions.jira.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2014-07-02
 */
public class Issue {
	private String key;
	private Fields fields;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}
}
