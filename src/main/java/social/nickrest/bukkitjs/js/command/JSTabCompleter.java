package social.nickrest.bukkitjs.js.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.graalvm.polyglot.Value;
import social.nickrest.bukkitjs.js.JSEngine;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
@AllArgsConstructor
public class JSTabCompleter {

    private final JSCommandBuilder commandBuilder;
    private final JSPlugin plugin;

    public Value value;

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(value == null) return null;

        Value obj = value.execute(sender, args);

        List<String> list = new ArrayList<>();

        if (obj.hasArrayElements()) {
            for (int i = 0; i < obj.getArraySize(); i++) {
                list.add(obj.getArrayElement(i).asString());
            }

            return list;
        }

        return null;
    }


}
