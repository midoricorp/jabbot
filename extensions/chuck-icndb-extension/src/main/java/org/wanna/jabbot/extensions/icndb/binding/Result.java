package org.wanna.jabbot.extensions.icndb.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-14
 */
public class Result {
	private String type;
	private Entry value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Entry getValue() {
		return value;
	}

	public void setValue(Entry value) {
		this.value = value;
	}
}
