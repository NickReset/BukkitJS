package social.nickrest.bukkitjs.command.commands;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.command.updated.UpdatedCommandExecutor;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;
import social.nickrest.bukkitjs.js.pnpm.PNPM;
import social.nickrest.bukkitjs.js.pnpm.PNPMDownloader;

import java.io.File;
import java.util.List;

@CommandInfo(name = "pnpm", description = "execute pnpm commands", permission = "bukkitjs.pnpm")
public class PNPMCommand extends UpdatedCommandExecutor {

    @Getter
    private final PNPM pnpm;

    public PNPMCommand() {
        super();

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
                pnpm.install((v) -> sender.sendMessage("§aInstalled " + pkg + "!"), pkg);
            }
            case "uninstall", "un" -> {
                String pkg = args[1];

                if(pkg == null) {
                    sender.sendMessage("§cUsage (PNPM): /pnpm <install/uninstall> <package>");
                    return false;
                }

                sender.sendMessage("§aUninstalling " + pkg + "...");
                pnpm.uninstall((v) -> sender.sendMessage("§cUninstalled " + pkg + "!"), pkg);
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
