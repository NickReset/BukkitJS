package social.nickrest.bukkitjs.js.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import social.nickrest.bukkitjs.BukkitJS;
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

    private final String name;

    private final Value function;
    private Value tabComplete;

    public JSCommand command;

    public JSCommandBuilder(JSPlugin plugin, @NotNull String name, Value function) {
        this.plugin = plugin;
        this.name = name;
        this.function = function;
    }

    public JSCommandBuilder aliases(String[] aliases) {
        values.put("aliases", List.of(aliases));
        return this;
    }

    public JSCommandBuilder description(String description) {
        values.put("description", description);
        return this;
    }

    public JSCommandBuilder permission(String permission) {
        values.put("permission", permission);
        return this;
    }

    public JSCommandBuilder permissionMessage(String permissionMessage) {
        values.put("permissionMessage", permissionMessage);
        return this;
    }

    public JSCommandBuilder usage(String usage) {
        values.put("usage", usage);
        return this;
    }

    public JSCommandBuilder tabComplete(Value value) {
        this.tabComplete = value;
        return this;
    }

    public JSCommand build() {
        BukkitJS plugin = BukkitJS.get();

        SimpleCommandMap commandMap = plugin.getCommandMap();
        JSCommand command = new JSCommand(this, name, function);

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

        if(commandMap == null) {
            throw new NullPointerException("CommandMap is null");
        }

        commandMap.register(plugin.getName().toLowerCase(), command);
        plugin.reloadAllCommands();

        this.plugin.getCommands().put(command.getName(), command);

        return command;
    }

}
