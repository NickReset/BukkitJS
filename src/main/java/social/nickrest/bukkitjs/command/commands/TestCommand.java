package social.nickrest.bukkitjs.command.commands;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import social.nickrest.bukkitjs.BukkitJS;
import social.nickrest.bukkitjs.command.updated.UpdatedCommandExecutor;
import social.nickrest.bukkitjs.command.updated.data.CommandInfo;
import social.nickrest.bukkitjs.js.node.JSEngineNode;

@CommandInfo(name = "test")
public class TestCommand extends UpdatedCommandExecutor {

    @Getter
    private final JSEngineNode engine;

    public TestCommand() {
        this.engine = new JSEngineNode();
        this.engine.put("Bukkit", Bukkit.class);
        this.engine.put("server", Bukkit.getServer());
        this.engine.put("BukkitJS", BukkitJS.get());
        this.engine.put("engine", this.engine);
    }

    @Override
    public boolean handle(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return false;
        }

        if(args.length == 0) {
            player.sendMessage("§cUsage: /test <script>");
            return false;
        }

        String script = String.join(" ", args);
        this.engine.eval(script);
        return false;
    }

}
