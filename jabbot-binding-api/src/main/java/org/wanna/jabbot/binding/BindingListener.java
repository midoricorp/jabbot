package org.wanna.jabbot.binding;

/**
 * @author vmorsiani <vmorsiani>
 * @since 2015-02-20
 */
public interface BindingListener {
	void onMessage(Binding binding,BindingMessage message);
}
