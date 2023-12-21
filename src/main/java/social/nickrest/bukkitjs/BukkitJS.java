package social.nickrest.bukkitjs;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.interop.V8Host;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import social.nickrest.bukkitjs.command.commands.PNPMCommand;
import social.nickrest.bukkitjs.command.commands.ScriptCommand;
import social.nickrest.bukkitjs.command.updated.CommandManager;
import social.nickrest.bukkitjs.classloader.BukkitJSClassloader;
import social.nickrest.bukkitjs.js.JSPlugin;
import social.nickrest.bukkitjs.js.pnpm.PNPM;

import java.io.*;
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
            BukkitJSClassloader vmClassLoader = new BukkitJSClassloader(this.getClassLoader());

            vmClassLoader.addURL(BukkitJS.locate(BukkitJS.class));
            Thread.currentThread().setContextClassLoader(vmClassLoader);

            V8Host v8Host = V8Host.getInstance(JSRuntimeType.Node);
            v8Host.loadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        CommandManager.register(
                new ScriptCommand(), new PNPMCommand()
        );

        this.load(null);
    }

    @Override
    public void onDisable() {
        plugins.forEach(JSPlugin::shutdown);
        CommandManager.onDisable();

        try {
            V8Host v8Host = V8Host.getInstance(JSRuntimeType.Node);
            v8Host.close();
            v8Host.unloadLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void load(CommandSender sender) {
        File file = new File(getDataFolder(), "scripts");

        if(!file.exists() && !file.mkdirs()) {
            getLogger().severe("Failed to create scripts folder!");
            return;
        }

        File scriptsJson = new File(file, "scripts.json");

        try {
            boolean create = !scriptsJson.exists();

            if (create && !scriptsJson.createNewFile()) {
                getLogger().severe("Failed to create scripts.json!");
                return;
            }

            if(create) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(scriptsJson));
                writer.write("[]");
                writer.close();
            }

            BufferedReader reader = new BufferedReader(new FileReader(scriptsJson));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONArray array = (JSONArray) new JSONParser().parse(builder.toString());

            for (Object object : array) {
                String name = (String) object;

                File scriptFile = new File(file, name);

                if(!scriptFile.exists()) {
                    getLogger().warning("Script file " + name + " does not exist!");
                    continue;
                }

                if(getScript(name) != null) {
                    JSPlugin plugin = getScript(name);
                    plugin.reload(null);
                    continue;
                }

                JSPlugin plugin = new JSPlugin(scriptFile);
                plugins.add(plugin);
            }

            reader.close();

            if(sender != null) {
                sender.sendMessage("§aReloaded scripts! and scripts.json");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public PNPM getPNPM() {
        return ((PNPMCommand) CommandManager.getCommand(PNPMCommand.class)).getPnpm();
    }

    public JSPlugin getScript(String name) {
        return plugins.stream().filter(plugin -> plugin.getName().equalsIgnoreCase(name) || plugin.getFile().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static BukkitJS get() {
        return BukkitJS.getPlugin(BukkitJS.class);
    }

}
