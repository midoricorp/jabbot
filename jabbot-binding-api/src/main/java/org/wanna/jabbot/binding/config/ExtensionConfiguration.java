package org.wanna.jabbot.binding.config;

import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-25
 */
public interface ExtensionConfiguration {
    enum Type{
        COMMAND
    }

    /**
     * Determines the Type of extension
     *
     * @see org.wanna.jabbot.binding.config.ExtensionConfiguration.Type
     * @return extension type
     */
    Type getType();

    /**
     * Returns the FQDN class name of the extension
     * @return
     */
    String getClassName();

    /**
     * Returns the name of the extension.
     * Extension name will be used to identify it within a binding and therefore must be unique to the binding
     *
     * @return name
     */
    String getName();

    /**
     * Optional configuration map which can be passed to an extension
     *
     * @return optional configuration map
     */
    Map<String,Object> getConfiguration();
}
