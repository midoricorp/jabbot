package org.wanna.jabbot.extensions.script;

import org.wanna.jabbot.binding.messaging.Message;
import org.wanna.jabbot.binding.messaging.body.TextBodyPart;
import org.wanna.jabbot.command.AbstractCommandAdapter;
import org.wanna.jabbot.command.Command;
import org.wanna.jabbot.command.CommandFactory;
import org.wanna.jabbot.command.behavior.CommandFactoryAware;
import org.wanna.jabbot.command.config.CommandConfig;
import org.wanna.jabbot.command.messaging.CommandMessage;
import org.wanna.jabbot.command.messaging.DefaultCommandMessage;

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
    public Message process(CommandMessage message) {
        String scriptName = message.getBody().trim();
        Map<String,Command> cmds = commandFactory.getAvailableCommands();

        Command oldCmd = cmds.get(scriptName);

        if (oldCmd == null) {
            // don't nuke core commands!
            Message msg = new DefaultCommandMessage();
            msg.addBody(new TextBodyPart("Delete: Command '" + scriptName + "' not found!"));
            return msg;
        }

        if (!(oldCmd instanceof ScriptScript)) {
            // don't nuke core commands!
            Message msg = new DefaultCommandMessage();
            msg.addBody(new TextBodyPart("Cannot remove non-script command '" + scriptName + "'"));
            return msg;
        }

        commandFactory.deregister(scriptName);

        File f = new File(scriptDir + File.separator + scriptName + ".ss");
        f.delete();

        Message msg = new DefaultCommandMessage();
        msg.addBody(new TextBodyPart("Script '" + scriptName + "' has been removed"));
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
