package social.nickrest.bukkitjs.js;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.graalvm.polyglot.Value;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.js.command.JSCommand;
import social.nickrest.bukkitjs.js.command.JSCommandBuilder;
import social.nickrest.bukkitjs.js.command.JSCommandExecutor;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

@Getter
public class JSPlugin {

    private final HashMap<Class<? extends Event>, Value> events = new HashMap<>();
    private final HashMap<String, JSCommand> commands = new HashMap<>();

    private final JSEngine engine;
    private final File file;

    public Object[] dependencies = {
            "plugin", this,
            "server", Bukkit.getServer(),
            "Bukkit", Bukkit.class
    };

    public JSPlugin(File file) {
        this.engine = new JSEngine();
        this.loadDependencies();
        this.engine.eval(this.file = file);
    }

    public void reload(CommandSender sender) {
        if(this.isRunning()) {
            this.shutdown();
        }

        this.engine.start();
        this.loadDependencies();
        this.engine.eval(file);

        if(sender != null) {
            sender.sendMessage(String.format("§aReloaded script %s", file.getName()));
        }
    }

    public void shutdown() {
        BukkitJS plugin = BukkitJS.get();

        if(plugin.getCommandMap() == null) {
            throw new NullPointerException(String.format("Failed to reload script %s because the command map is null", file.getName()));
        }

        this.commands.values().forEach(command -> command.unregister(plugin.getCommandMap()));
        this.events.clear();
        this.engine.terminate();

        plugin.reloadAllCommands();
    }

    private void loadDependencies() {
        for(int i = 0; i < dependencies.length; i += 2) {
            String name = (String) dependencies[i];
            Object object = dependencies[i + 1];

            if(object instanceof Class) {
                this.engine.put(name, (Class<?>) object);
                continue;
            }

            this.engine.put(name, object);
        }
    }

    @SuppressWarnings("unused") // called by JS
    public void on(Class<? extends Event> eventClass, Value function) {
        BukkitJS plugin = BukkitJS.get();

        if(events.get(eventClass) != null)
            throw new IllegalArgumentException(String.format("Event %s is already registered", eventClass.getName()));

        plugin.getServer().getPluginManager().registerEvent(eventClass, BukkitJS.get(), EventPriority.NORMAL, ((listener, e) -> function.execute(e)), BukkitJS.get());
        events.put(eventClass, function);
    }

    @SuppressWarnings("unused") // called by JS
    public JSCommandBuilder command(String commandName, Value function) {
        if(commands.get(commandName) != null) {
            JSCommand command = commands.get(commandName);
            SimpleCommandMap map = BukkitJS.get().getCommandMap();

            if(map == null) {
                throw new NullPointerException("Failed to reload command " + commandName + " because the command map is null");
            }

            command.unregister(map);

            command.setFunction(function);
            command.setCommandExecutor(new JSCommandExecutor(function));

            JSCommandBuilder builder = new JSCommandBuilder(this, this.engine, commandName, function);

            try {
                Method method = builder.getClass().getDeclaredMethod("command", JSCommand.class);
                method.setAccessible(true);

                method.invoke(builder, command);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return builder;
        }

        return new JSCommandBuilder(this, this.engine, commandName, function);
    }

    public String getName() {
        return this.file.getName().substring(0, this.file.getName().lastIndexOf("."));
    }

    public boolean isRunning() {
        return this.engine.isRunning();
    }
}
