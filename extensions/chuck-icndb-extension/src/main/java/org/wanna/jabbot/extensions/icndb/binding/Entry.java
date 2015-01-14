package org.wanna.jabbot.extensions.icndb.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-01-14
 */
public class Entry {
	private long id;
	private String joke;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getJoke() {
		return joke;
	}

	public void setJoke(String joke) {
		this.joke = joke;
	}
}
