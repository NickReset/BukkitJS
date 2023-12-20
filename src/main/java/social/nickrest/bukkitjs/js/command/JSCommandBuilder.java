package social.nickrest.bukkitjs.js.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.js.JSEngine;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Getter
@SuppressWarnings({ "unused" }) // setting unused to prevent warning cause this is called by scripts
public class JSCommandBuilder {

    private final HashMap<String, Object> values = new HashMap<>();

    private final JSPlugin plugin;
    private final JSEngine engine;

    private final String name;

    private final Value function;
    private Value tabComplete;

    public JSCommand command;

    public JSCommandBuilder(JSPlugin plugin, JSEngine engine, @NotNull String name, Value function) {
        this.plugin = plugin;
        this.engine = engine;
        this.name = name;
        this.function = function;
    }

    private JSCommandBuilder command(JSCommand command) {
        this.command = command;
        return this;
    }

    public JSCommandBuilder aliases(String[] aliases) {
        if(command != null) {
            command.setAliases(List.of(aliases));
            return this;
        }

        values.put("aliases", List.of(aliases));
        return this;
    }

    public JSCommandBuilder description(String description) {
        if(command != null) {
            command.setDescription(description);
            return this;
        }

        values.put("description", description);
        return this;
    }

    public JSCommandBuilder permission(String permission) {
        if(command != null) {
            command.setPermission(permission);
            return this;
        }

        values.put("permission", permission);
        return this;
    }

    public JSCommandBuilder permissionMessage(String permissionMessage) {
        if(command != null) {
            command.setPermissionMessage(permissionMessage);
            return this;
        }

        values.put("permissionMessage", permissionMessage);
        return this;
    }

    public JSCommandBuilder usage(String usage) {
        if(command != null) {
            command.setUsage(usage);
            return this;
        }

        values.put("usage", usage);
        return this;
    }

    public JSCommandBuilder tabComplete(Value value) {
        if(command != null) {
            command.setTabComplete(value);
            return this;
        }

        this.tabComplete = value;
        return this;
    }

    public JSCommand build() {
        BukkitJS plugin = BukkitJS.get();
        SimpleCommandMap commandMap = plugin.getCommandMap();

        if(commandMap == null) {
            throw new NullPointerException("Cannot build command while the command map is null");
        }

        if(command != null) {
            commandMap.register(plugin.getName().toLowerCase(), command);
            plugin.reloadAllCommands();

            return command;
        }

        JSCommand command = new JSCommand(this, name, function, tabComplete, this.plugin, engine);

        for(String key : values.keySet()) {
            Object value = values.get(key);

            String filedName = String.format("set%s%s", key.substring(0, 1).toUpperCase(), key.substring(1));

            try {
                Field field = command.getClass().getDeclaredField(key);
                field.setAccessible(true);

                field.set(command, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        commandMap.register(plugin.getName().toLowerCase(), command);
        plugin.reloadAllCommands();

        this.plugin.getCommands().put(command.getName(), command);

        return command;
    }

}
