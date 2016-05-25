package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.messaging.RxMessage;
import org.wanna.jabbot.messaging.Resource;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class MessageEvent extends AbstractBindingEvent<RxMessage>{
	private Resource source;

	public MessageEvent(Binding binding, RxMessage message) {
		super(binding,message);
	}

	public MessageEvent(Binding binding, RxMessage message, Resource source) {
		super(binding,message);
		this.source = source;
	}

	public Resource getSource() {
		return source;
	}
}
