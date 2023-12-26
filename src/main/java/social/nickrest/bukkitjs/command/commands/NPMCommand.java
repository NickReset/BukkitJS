package social.nickrest.bukkitjs.command.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.command.updated.UpdatedCommandExecutor;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;
import social.nickrest.bukkitjs.js.NPMLog4jLogger;
import social.nickrest.npm.NPM;

import java.io.File;
import java.util.List;

@CommandInfo(name = "npm", description = "execute npm commands", permission = "bukkitjs.npm")
public class NPMCommand extends UpdatedCommandExecutor {

    @Getter
    private final NPM npm;

    public NPMCommand() {
        this.npm = new NPM(new File(BukkitJS.get().getDataFolder(), "scripts\\node_modules"));
        this.npm.setLogger(new NPMLog4jLogger());
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage("§cUsage (PNPM): /pnpm <install/uninstall> <package>");
            return false;
        }

        String action = args[0];
        switch (action) {
            case "install", "i" -> {
                String pkg = args[1];

                if(pkg == null) {
                    sender.sendMessage("§cUsage (PNPM): /pnpm <install/uninstall> <package>");
                    return false;
                }

                sender.sendMessage("§aInstalling " + pkg + "...");
                npm.getPackage(pkg)
                        .await()
                        .install((v) -> sender.sendMessage("§aInstalled " + pkg + "!"));
            }
            case "uninstall", "un" -> {
                String pkg = args[1];

                if(pkg == null) {
                    sender.sendMessage("§cUsage (PNPM): /pnpm <install/uninstall> <package>");
                    return false;
                }

                sender.sendMessage("§aUninstalling " + pkg + "...");
                npm.getInstalledPackage(pkg)
                        .uninstall((v) -> sender.sendMessage("§aUninstalled " + pkg + "!"));
            }
            default -> sender.sendMessage("§cUsage (PNPM): /pnpm <install/uninstall> <package>");
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> newCompletion = List.of("install", "uninstall");

        if (args.length == 1) {
            return newCompletion;
        }

        return List.of();
    }
}
