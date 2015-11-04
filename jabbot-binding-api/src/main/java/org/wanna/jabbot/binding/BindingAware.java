package org.wanna.jabbot.binding;

/**
 * Interface which notifies a builder that a class should get an instance of the binding injected
 *
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-09-08
 */
public interface BindingAware {
    void setBinding(Binding binding);
}
