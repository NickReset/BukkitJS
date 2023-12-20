package social.nickrest.bukkitjs.js.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import social.nickrest.bukkitjs.js.JSEngine;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.util.List;

@Setter @Getter
public class JSCommand extends Command {

    private JSCommandExecutor commandExecutor;
    private JSCommandBuilder commandBuilder;
    private JSTabCompleter tabCompleter;
    private JSPlugin plugin;

    private JSEngine engine;

    private Value function, tabComplete;

    public JSCommand(JSCommandBuilder commandBuilder, @NotNull String name, Value function, Value tabComplete, JSPlugin plugin, JSEngine engine) {
        super(name);

        this.plugin = plugin;
        this.engine = engine;
        this.commandBuilder = commandBuilder;
        this.commandExecutor = new JSCommandExecutor(function);

        if(tabComplete != null) {
            this.tabCompleter = new JSTabCompleter(commandBuilder, plugin, tabComplete);
            this.tabComplete = tabComplete;
        }

        this.function = function;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return commandExecutor.execute(sender, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        if(tabCompleter != null) {
            return tabCompleter.tabComplete(sender, args);
        }

        return super.tabComplete(sender, alias, args, location);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    public void setTabComplete(Value tabComplete) {
        this.tabComplete = tabComplete;
        this.tabCompleter = new JSTabCompleter(commandBuilder, plugin, tabComplete);
    }
}
