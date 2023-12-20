package social.nickrest.bukkitjs;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import social.nickrest.bukkitjs.command.commands.ScriptCommand;
import social.nickrest.bukkitjs.command.updated.CommandManager;
import social.nickrest.bukkitjs.js.classloader.GraalVMClassLoader;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.DriverManager;
import java.util.*;

public final class BukkitJS extends JavaPlugin implements Listener {

    @Getter
    private final List<JSPlugin> plugins = new ArrayList<>();

    @Override
    public void onLoad() {
        DriverManager.getDrivers();

        try {
            GraalVMClassLoader vmClassLoader = new GraalVMClassLoader(this.getClassLoader());

            vmClassLoader.addURL(BukkitJS.locate(BukkitJS.class));
            Thread.currentThread().setContextClassLoader(vmClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        CommandManager.register(
                new ScriptCommand()
        );

        File file = new File(getDataFolder(), "scripts");

        if(!file.exists() && !file.mkdirs()) {
            getLogger().severe("Failed to create scripts folder!");
            return;
        }

        for(File script : Objects.requireNonNull(file.listFiles())) {
            if (!script.getName().endsWith(".js")) continue;

            plugins.add(new JSPlugin(script));
        }
    }

    @Override
    public void onDisable() {
        plugins.forEach(JSPlugin::shutdown);
        CommandManager.onDisable();
    }

    public JSPlugin getScript(String name) {
        return plugins.stream().filter(plugin -> plugin.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public SimpleCommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);

            return field.get(Bukkit.getServer()) == null ? null : (SimpleCommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void unregisterCommand(String name) {
        SimpleCommandMap commandMap = this.getCommandMap();

        if(commandMap == null) {
            throw new NullPointerException("CommandMap is null");
        }

        Command command = commandMap.getCommand(name);

        if(command == null) {
            throw new NullPointerException("Command is null");
        }

        command.unregister(commandMap);
    }

    public void unregisterCommand(Command command) {
        this.unregisterCommand(command.getName());
    }

    public void reloadAllCommands() {
        try {
            Class<?> craftServer = Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".CraftServer");

            Method syncCommandsMethod = craftServer.getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static URL locate(Class<?> clazz) {
        try {
            URL resource = clazz.getProtectionDomain().getCodeSource().getLocation();
            if (resource != null) return resource;
        } catch (SecurityException | NullPointerException ignored) {}

        URL resource = clazz.getResource(clazz.getSimpleName() + ".class");

        if (resource == null) throw new IllegalArgumentException("Cannot locate " + clazz.getCanonicalName());

        String link = resource.toString();
        String suffix = String.format("%s.class", clazz.getCanonicalName().replace('.', '/'));
        if (link.endsWith(suffix)) {
            String path = link.substring(0, link.length() - suffix.length());

            if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

            try {
                return new URL(path);
            } catch (Throwable ignored) {}
        }

        return null;
    }

    public static BukkitJS get() {
        return BukkitJS.getPlugin(BukkitJS.class);
    }

}
