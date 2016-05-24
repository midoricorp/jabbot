package org.wanna.jabbot.binding.event;

import org.wanna.jabbot.binding.Binding;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2016-03-03
 */
public abstract class AbstractBindingEvent<T> implements BindingEvent<T>{
	private Binding binding;
	private T payload;

	public AbstractBindingEvent(Binding binding, T payload) {
		this.binding = binding;
		this.payload = payload;
	}

	@Override
	public Binding getBinding() {
		return binding;
	}

	@Override
	public T getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer(this.getClass().getSimpleName()+"{");
		sb.append("binding=").append(binding);
		sb.append(", payload=").append(payload);
		sb.append('}');
		return sb.toString();
	}
}
