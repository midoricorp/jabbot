package org.wanna.jabbot.extensions.foaas.binding;

import java.util.List;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-07
 */
public class Operation {
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

}
