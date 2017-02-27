package org.wanna.jabbot.messaging;

/**
 * Default implementation of RoutableMessageContent
 *
 * @author vmorsiani
 * @since 2017-02-26
 */
public class DefaultRoutableMessageContent extends  DefaultMessageContent implements RoutableMessageContent{
	private final String bindingId;
	private final String resourceId;

	public DefaultRoutableMessageContent(String bindingId, String resourceId, String text) {
		super(text);
		this.bindingId = bindingId;
		this.resourceId = resourceId;
	}

	@Override
	public String getBindingId() {
		return bindingId;
	}

	@Override
	public String getResourceId() {
		return resourceId;
	}
}
