package social.nickrest.bukkitjs.js.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import social.nickrest.bukkitjs.js.JSPlugin;

import java.util.List;

@Setter @Getter
@AllArgsConstructor
public class JSTabCompleter {

    private final JSCommandBuilder commandBuilder;
    private final JSPlugin plugin;

    public String tabCompleteSrc;

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(tabCompleteSrc == null) return null;

        try {
            Object obj = plugin.getEngine().runFunctionFromSrc(tabCompleteSrc, "tabComplete", sender, args);

            if(obj instanceof List) {
                return (List<String>) obj;
            }

            return List.of();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
