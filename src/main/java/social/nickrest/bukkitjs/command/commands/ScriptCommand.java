package social.nickrest.bukkitjs.command.commands;

import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.command.updated.UpdatedCommandExecutor;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.util.List;

@CommandInfo(name = "script", permission = "bukkitjs.script")
public class ScriptCommand extends UpdatedCommandExecutor {

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUsage: /script <reload/disable/enable> <script.js>");
            return false;
        }

        String action = args[0];
        String scriptName = args[1];

        JSPlugin plugin = BukkitJS.get().getScript(scriptName);

        if (plugin == null) {
            sender.sendMessage("§cScript not found!");
            return false;
        }

        switch (action.toLowerCase()) {
            case "reload" -> {
                sender.sendMessage("§aReloading...");
                plugin.reload(sender);
            }
            case "disable" -> {
                if (!plugin.isRunning()) {
                    sender.sendMessage("§cScript is already disabled!");
                    return false;
                }
                plugin.shutdown();
                sender.sendMessage("§aScript disabled!");
            }
            case "enable" -> {
                if (plugin.isRunning()) {
                    sender.sendMessage("§cScript is already enabled!");
                    return false;
                }
                plugin.reload(null);
                sender.sendMessage("§aScript enabled!");
            }
            default -> sender.sendMessage("§cUsage: /script <reload/disable/enable> <script.js>");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completions = List.of("enable", "disable", "reload");

        if (args.length == 1) {
            return completions;
        }

        if(args.length == 2) {
            return BukkitJS.get().getPlugins().stream().map(JSPlugin::getName).toList();
        }

        return null;
    }
}
