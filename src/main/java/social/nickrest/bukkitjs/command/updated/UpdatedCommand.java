package social.nickrest.bukkitjs.command.updated;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;

import java.util.List;

@SuppressWarnings("deprecation")
public class UpdatedCommand extends Command {

    private final UpdatedCommandExecutor executor;

    public UpdatedCommand(UpdatedCommandExecutor executor, CommandInfo info) {
        super(info.name(), info.description(), info.usage(), List.of(info.aliases()));

        if (!info.permission().isEmpty()) {
            this.setPermission(info.permission());
        }

        if (!info.permissionMessage().isEmpty()) {
            this.setPermissionMessage(info.permissionMessage());
        }

        this.executor = executor;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return this.executor.handle(sender, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return this.executor.tabComplete(sender, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return this.executor.tabComplete(sender, args);
    }
}
