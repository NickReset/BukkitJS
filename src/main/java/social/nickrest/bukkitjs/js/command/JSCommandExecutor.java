package social.nickrest.bukkitjs.js.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import social.nickrest.bukkitjs.js.node.JSEngineNode;

public record JSCommandExecutor(JSEngineNode engine, String source) {

    public boolean execute(CommandSender sender, @NotNull String[] args) {
        try {
            Object result = engine.runFunctionFromSrc(source, "command", sender, args);

            if(result instanceof Boolean) {
                return (boolean) result;
            }

            return true;
        } catch (Exception e) {
            sender.sendMessage(String.format("§cError in script (command): %s", e.getMessage()));
            throw new RuntimeException(e);
        }
    }

}
