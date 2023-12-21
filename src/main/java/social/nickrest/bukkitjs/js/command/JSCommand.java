package social.nickrest.bukkitjs.js.command;

import com.caoccao.javet.values.reference.V8ValueFunction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import social.nickrest.bukkitjs.js.JSPlugin;
import social.nickrest.bukkitjs.js.node.JSEngineNode;

import java.util.List;

@Setter @Getter
public class JSCommand extends Command {

    private JSCommandExecutor commandExecutor;
    private JSCommandBuilder commandBuilder;
    private JSTabCompleter tabCompleter;
    private JSPlugin plugin;

    private JSEngineNode engine;

    private String functionSrc, tabCompleteSrc;

    public JSCommand(JSCommandBuilder commandBuilder, @NotNull String name, String functionSrc, String tabCompleteSrc, JSPlugin plugin, JSEngineNode engine) {
        super(name);

        this.plugin = plugin;
        this.engine = engine;
        this.commandBuilder = commandBuilder;
        this.commandExecutor = new JSCommandExecutor(engine, functionSrc);

        if(tabCompleteSrc != null) {
            this.tabCompleter = new JSTabCompleter(commandBuilder, plugin, tabCompleteSrc);
            this.tabCompleteSrc = tabCompleteSrc;
        }

        this.functionSrc = functionSrc;
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

        return List.of();
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    public void setTabComplete(String tabCompleteSrc) {
        this.tabCompleteSrc = tabCompleteSrc;
        this.tabCompleter = new JSTabCompleter(commandBuilder, plugin, tabCompleteSrc);
    }
}
