package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.messaging.DefaultMessageContent;
import org.wanna.jabbot.messaging.MessageContent;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;

import java.io.File;
import java.util.Map;

/**
 * @author tsearle
 * @since 20/04/16
 */
public class DeleteCommand  extends AbstractCommandAdapter implements CommandFactoryAware {
    private CommandFactory commandFactory;
    private String scriptDir;

    public DeleteCommand(CommandConfig configuration) {
        super(configuration);
    }

    @Override
    public MessageContent process(CommandMessage message) {
        String scriptName = message.getArgsLine().trim();
        Map<String,Command> cmds = commandFactory.getAvailableCommands();

        Command oldCmd = cmds.get(scriptName);

        if (oldCmd == null) {
            // don't nuke core commands!
            MessageContent msg = new DefaultMessageContent("Delete: Command '" + scriptName + "' not found!");
            return msg;
        }

        if (!(oldCmd instanceof ScriptScript)) {
            // don't nuke core commands!
            MessageContent msg = new DefaultMessageContent("Cannot remove non-script command '" + scriptName + "'");
            return msg;
        }

        commandFactory.deregister(scriptName);

        File f = new File(scriptDir + File.separator + scriptName + ".ss");
        f.delete();

        MessageContent msg = new DefaultMessageContent("Script '" + scriptName + "' has been removed");
        return msg;
    }

    @Override
    public void configure(Map<String, Object> configuration) {
        if (configuration == null ) return;
        if (configuration.containsKey("script_dir")) {
            scriptDir = configuration.get("script_dir").toString();
        }
    }

    @Override
    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }
}
