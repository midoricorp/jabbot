package org.wanna.jabbot.binding.config;

import java.util.Map;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-25
 */
public class ExtensionConfiguration {
	/**
	 * Determine the type of extension
	 */
    enum Type{
		/** Command extension type **/
        command,
		/** Binding extension type **/
		binding
    }

    private Type type;
    private String className;
    private String name;
    private Map<String,Object> configuration;

    /**
     * Determines the Type of extension
     *
     * @see org.wanna.jabbot.binding.config.ExtensionConfiguration.Type
     * @return extension type
     */
    public Type getType(){
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the FQDN class name of the extension
     * @return
     */
    public String getClassName(){
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the name of the extension.
     * Extension name will be used to identify it within a binding and therefore must be unique to the binding
     *
     * @return name
     */
    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Optional configuration map which can be passed to an extension
     *
     * @return optional configuration map
     */
    public Map<String,Object> getConfiguration(){
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }
}
