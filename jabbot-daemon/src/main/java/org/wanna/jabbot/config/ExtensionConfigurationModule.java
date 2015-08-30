package org.wanna.jabbot.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.command.config.CommandConfig;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-25
 */
public class ExtensionConfigurationModule extends SimpleModule{
    public ExtensionConfigurationModule() {
        super("extension module");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(ExtensionConfiguration.class,ExtensionConfigMixin.class);
        super.setupModule(context);
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type",
            defaultImpl = ExtensionConfiguration.Type.class,
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(name="command",value = CommandConfig.class)
    })
    public abstract class ExtensionConfigMixin{

    }
}
