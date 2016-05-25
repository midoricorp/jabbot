package org.wanna.jabbot.messaging.body;

import java.util.HashMap;

/**
 * Factory class responsible of creating BodyPartValidator
 *
 * @see {@link BodyPartValidator}
 * @author Vincent Morsiani
 * @since 2015-07-24
 */
public class BodyPartValidatorFactory {
    private static BodyPartValidatorFactory instance = new BodyPartValidatorFactory();
    private HashMap<BodyPart.Type,BodyPartValidator> registry;

    /**
     * Gets an instance of the BodyPartValidatorFactory
     * @return factory instance
     */
    public static BodyPartValidatorFactory getInstance(){
        return instance;
    }

    /**
     * Create an instance of the BodyPartValidatorFactory and register validators into it
     */
    public BodyPartValidatorFactory(){
        registry = new HashMap<>();

        register(BodyPart.Type.XHTML, new XhtmlFragmentValidator());
    }

    /**
     * Create a BodyPartValidator.
     * It will check the registry and returns the validator matching the type.
     * If no validator is found, NULL will be returned
     *
     * @param type BodyPart type
     * @return BodyPartValidator
     */
    public BodyPartValidator create(BodyPart.Type type){
        return registry.get(type);
    }

    /**
     * register a new BodyPartValidator into the factory
     *
     * @param type BodyPart type to which the validator will be bound
     * @param validator BodyPartValidator to register
     */
    public void register(BodyPart.Type type, BodyPartValidator validator){
        registry.put(type,validator);
    }

}
