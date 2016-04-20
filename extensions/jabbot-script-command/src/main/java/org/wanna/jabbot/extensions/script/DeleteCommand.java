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

import java.util.Map;

/**
 * @author tsearle
 * @since 20/04/16
 */
public class DeleteCommand  extends AbstractCommandAdapter implements CommandFactoryAware {
    private CommandFactory commandFactory;

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

        Message msg = new DefaultCommandMessage();
        msg.addBody(new TextBodyPart("Script '" + scriptName + "' has been removed"));
        return msg;
    }

    @Override
    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }
}
