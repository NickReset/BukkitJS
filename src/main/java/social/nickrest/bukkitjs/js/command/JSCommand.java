package social.nickrest.bukkitjs.js.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class JSCommand extends Command {

    private JSCommandExecutor commandExecutor;
    private JSCommandBuilder commandBuilder;

    private Value function;

    public JSCommand(JSCommandBuilder commandBuilder, @NotNull String name, Value function) {
        super(name);

        this.commandBuilder = commandBuilder;
        this.commandExecutor = new JSCommandExecutor(function);
        this.function = function;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        return commandExecutor.execute(sender, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        Value value = commandBuilder.getTabComplete();
        if(value == null) return super.tabComplete(sender, alias, args, location);

        Value obj = value.execute(sender, args);

        List<String> list = new ArrayList<>();

        if(obj.hasArrayElements()) {
            for (int i = 0; i < obj.getArraySize(); i++) {
                list.add(obj.getArrayElement(i).asString());
            }

            return list;
        }

        return super.tabComplete(sender, alias, args, location);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }
}
