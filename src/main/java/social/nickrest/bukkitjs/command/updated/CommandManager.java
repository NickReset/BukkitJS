package social.nickrest.bukkitjs.command.updated;

import lombok.Getter;
import org.bukkit.command.SimpleCommandMap;
import social.nickrest.bukkitjs.BukkitJS;

import java.util.HashMap;
import java.util.List;

public class CommandManager {

    @Getter
    private static final HashMap<String, UpdatedCommandExecutor> registeredCommands = new HashMap<>();

    public static void register(UpdatedCommandExecutor... command) {
        List.of(command).forEach(CommandManager::register);
    }

    public static void register(UpdatedCommandExecutor executor) {
        BukkitJS plugin = BukkitJS.get();

        SimpleCommandMap commandMap = plugin.getCommandMap();

        if(commandMap == null) {
            throw new NullPointerException("CommandMap is null");
        }

        commandMap.register(plugin.getName().toLowerCase(), executor.getCommand());
        plugin.reloadAllCommands();
    }

    public static void unregister(UpdatedCommandExecutor command) {
        registeredCommands.remove(command.getCommand().getName());
    }

    public static void onDisable() {
        BukkitJS plugin = BukkitJS.get();

        registeredCommands.values().forEach(command -> {
            plugin.unregisterCommand(command.getCommand());
            unregister(command);
        });
    }
}
