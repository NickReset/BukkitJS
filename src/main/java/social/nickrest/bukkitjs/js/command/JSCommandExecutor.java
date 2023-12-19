package social.nickrest.bukkitjs.js.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;

public class JSCommandExecutor {

    @Getter
    private Value function;

    public JSCommandExecutor(Value function) {
        this.function = function;
    }

    public boolean execute(CommandSender sender, @NotNull String[] args) {
        Value obj = function.execute(sender, args);

        if(obj.isBoolean()) {
            return obj.asBoolean();
        }

        return false;
    }

}
