package org.wanna.jabbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wanna.jabbot.binding.Binding;
import org.wanna.jabbot.binding.BindingAware;
import org.wanna.jabbot.binding.config.ExtensionConfiguration;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.behavior.Configurable;
import org.wanna.jabbot.command.config.CommandConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Vincent Morsiani [vmorsiani@voxbone.com]
 * @since 2015-08-24
 */
public class CommandManager {
    private final static Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private static final Map<Binding,CommandManager> instances = new WeakHashMap<>();
    private CommandFactory commandFactory;
    private Binding binding;

    public CommandManager(Binding binding) {
        this.binding = binding;
        commandFactory = new JabbotCommandFactory();
    }

    public static CommandManager getInstanceFor(Binding binding){
        if(!instances.containsKey(binding)){
            CommandManager manager = new CommandManager(binding);
            instances.put(binding,manager);
            return manager;
        }
        return instances.get(binding);
    }

    /**
     * Retrieve the command factory associated to a given binding
     *
     * @return Command factory
     */
    public CommandFactory getCommandFactory(){
        return commandFactory;
    }

    /**
     * Initialize a CommandFactory of a specific binding with a list of defined Commands.
     * Commands are created using the provided CommandConfig and configured upon creation.
     *
     * @param configSet set of command configuration used for initializing the CommandFactory
     */
    public void initializeFromConfigSet(Set<ExtensionConfiguration> configSet) {
        for (ExtensionConfiguration commandConfig : configSet) {
            try {
                @SuppressWarnings("unchecked")
                Class<Command> commandClass = (Class<Command>) Class.forName(commandConfig.getClassName());
                Command command = commandClass.getDeclaredConstructor(CommandConfig.class).newInstance(commandConfig);
                if (command instanceof CommandFactoryAware) {
                    ((CommandFactoryAware) command).setCommandFactory(commandFactory);
                }

                if (command instanceof Configurable) {
                    ((Configurable) command).configure(commandConfig.getConfiguration());
                }

                if( command instanceof BindingAware){
                    ((BindingAware)command).setBinding(binding);
                }
                commandFactory.register(commandConfig.getName(), command);
                logger.info("registered command {} with alias '{}' in {}",command,commandConfig.getName(),binding);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                logger.warn("failed to register command with config {}",commandConfig,e);
            }
        }
    }
}

