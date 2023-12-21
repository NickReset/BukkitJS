package social.nickrest.bukkitjs.command.commands;

import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.command.updated.UpdatedCommandExecutor;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;
import social.nickrest.bukkitjs.js.JSPlugin;
import social.nickrest.bukkitjs.js.pnpm.PNPM;
import social.nickrest.bukkitjs.js.pnpm.PNPMDownloader;

import java.io.File;
import java.util.List;

@CommandInfo(name = "script", permission = "bukkitjs.script")
public class ScriptCommand extends UpdatedCommandExecutor {

    private final PNPM pnpm;

    public ScriptCommand() {
        File installedAt = new File(BukkitJS.get().getDataFolder(), "\\pnpm");

        if(!installedAt.exists() && !installedAt.mkdirs()) {
            throw new RuntimeException("Could not create PNPM directory");
        }

        if(!PNPMDownloader.isInstalled(installedAt)) {
            PNPMDownloader.installPNPM(installedAt);
        }

        this.pnpm = new PNPM(new File(BukkitJS.get().getDataFolder(), "\\scripts"), installedAt);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUsage (Script): /script <reload/disable/enable> <script.js>");
            sender.sendMessage("§cUsage (PNPM): /script <install/uninstall> <package>");
            return false;
        }

        String action = args[0];

        if((action.equalsIgnoreCase("install") || action.equalsIgnoreCase("uninstall")) || (action.equalsIgnoreCase("i") || action.equalsIgnoreCase("un"))) {
            String pkg = args[1];

            switch (action.toLowerCase()) {
                case "i", "install" -> {
                    sender.sendMessage("§aInstalling " + pkg + "...");
                    pnpm.install((v) -> sender.sendMessage("§aInstalled " + pkg + "!"), pkg);
                }

                case "uninstall", "un" -> {
                    sender.sendMessage("§aUninstalling " + pkg + "...");
                    pnpm.uninstall((v) -> sender.sendMessage("§cUninstalled " + pkg + "!"), pkg);
                }
            }

            return false;
        }

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
        List<String> completions = List.of("enable", "disable", "reload", "install", "uninstall");

        if (args.length == 1) {
            return completions;
        }

        if(args.length == 2) {
            if((args[0].equalsIgnoreCase("install") || args[0].equalsIgnoreCase("uninstall")) || (args[0].equalsIgnoreCase("i") || args[0].equalsIgnoreCase("un"))) {
                return List.of();
            }

            return BukkitJS.get().getPlugins().stream().map(JSPlugin::getName).toList();
        }

        return List.of();
    }
}
