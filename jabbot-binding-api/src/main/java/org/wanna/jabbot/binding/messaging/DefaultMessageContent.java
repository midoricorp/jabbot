package org.wanna.jabbot.binding.messaging;

import org.wanna.jabbot.binding.messaging.body.BodyPart;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public class DefaultMessageContent implements MessageContent{
	private Map<BodyPart.Type,BodyPart> bodies;

	public DefaultMessageContent() {
		this.bodies = new HashMap<>();
	}

	public DefaultMessageContent(String text) {
		this(new TextBodyPart(text));
	}

	public DefaultMessageContent(BodyPart bodyPart) {
		bodies = new HashMap<>();
		addBody(bodyPart);
	}

	@Override
	public String getBody() {
		BodyPart bodyPart = getBody(BodyPart.Type.TEXT);
		if(bodyPart == null ){
			return null;
		}else{
			return bodyPart.getText();
		}
	}

	@Override
	public BodyPart getBody(BodyPart.Type type) {
		return bodies.get(type);
	}

	@Override
	public Collection<BodyPart> getBodies() {
		return bodies.values();
	}

	@Override
	public void addBody(BodyPart body) {
		bodies.put(body.getType(),body);
	}
}
