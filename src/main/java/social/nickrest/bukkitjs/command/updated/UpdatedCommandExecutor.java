package social.nickrest.bukkitjs.command.updated;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;

import java.util.List;

@Getter
public abstract class UpdatedCommandExecutor {

    private final UpdatedCommand command;

    public UpdatedCommandExecutor() {
        CommandInfo info = this.getClass().getAnnotation(CommandInfo.class);

        if(info == null) {
            throw new IllegalArgumentException("CommandInfo annotation not found!");
        }

        this.command = new UpdatedCommand(this, info);
    }

    public abstract boolean handle(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
