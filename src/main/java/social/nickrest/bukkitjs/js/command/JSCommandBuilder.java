package social.nickrest.bukkitjs.js.command;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.values.reference.V8ValueFunction;
import lombok.Getter;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.js.JSPlugin;
import social.nickrest.bukkitjs.js.node.JSEngineNode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

@Getter
@SuppressWarnings({ "unused", "deprecation" }) // setting unused to prevent warning cause this is called by scripts
public class JSCommandBuilder {

    private final HashMap<String, Object> values = new HashMap<>();

    private final JSPlugin plugin;
    private final JSEngineNode engine;

    private final String name;
    private String functionSrc, tabCompleteSrc;

    public JSCommand command;

    public JSCommandBuilder(JSPlugin plugin, JSEngineNode engine, @NotNull String name, String functionSrc) {
        this.plugin = plugin;
        this.engine = engine;
        this.name = name;
        this.functionSrc = functionSrc;
    }

    private JSCommandBuilder command(JSCommand command) {
        this.command = command;
        return this;
    }

    @V8Function
    public JSCommandBuilder aliases(String[] aliases) {
        if(command != null) {
            command.setAliases(List.of(aliases));
            return this;
        }

        values.put("aliases", List.of(aliases));
        return this;
    }

    @V8Function
    public JSCommandBuilder description(String description) {
        if(command != null) {
            command.setDescription(description);
            return this;
        }

        values.put("description", description);
        return this;
    }

    @V8Function
    public JSCommandBuilder permission(String permission) {
        if(command != null) {
            command.setPermission(permission);
            return this;
        }

        values.put("permission", permission);
        return this;
    }

    @V8Function
    public JSCommandBuilder permissionMessage(String permissionMessage) {
        if(command != null) {
            command.setPermissionMessage(permissionMessage);
            return this;
        }

        values.put("permissionMessage", permissionMessage);
        return this;
    }

    @V8Function
    public JSCommandBuilder usage(String usage) {
        if(command != null) {
            command.setUsage(usage);
            return this;
        }

        values.put("usage", usage);
        return this;
    }

    @V8Function
    public JSCommandBuilder tabComplete(V8ValueFunction function) {
        try {
            if (command != null) {
                command.setTabComplete(function.getSourceCode());
                return this;
            }

            this.tabCompleteSrc = function.getSourceCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @V8Function
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

        JSCommand command = new JSCommand(this, name, functionSrc, tabCompleteSrc, this.plugin, engine);

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
