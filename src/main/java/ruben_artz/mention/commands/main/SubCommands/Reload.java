package ruben_artz.mention.commands.main.SubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.commands.main.SubCommand;
import ruben_artz.mention.util.UtilityFunctions;
import ruben_artz.mention.util.addColor;

import java.util.Objects;

public class Reload extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Reload() {
        super("reload:rl", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.SETTINGS.RELOAD_SOUND")), (Player) sender);
            }
            UtilityFunctions.LoadConfigCommand();
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_SUCCESS")));
            return;
        }
        if (args.length >= 2) {
            if ((args.length == 2) && (args[0].equalsIgnoreCase("reload"))) {
                if (args[1].equalsIgnoreCase("config")) {
                    UtilityFunctions.LoadConfigCommand();
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_RELOAD_CONFIGS").replace("{File}", args[1] + ".yml")));
                    return;
                } else if (args[1].equalsIgnoreCase("language")) {
                    plugin.initiate();
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_RELOAD_CONFIGS").replace("{File}", args[1] + ".file")));
                    return;
                } else if (args[1].equalsIgnoreCase("groups")) {
                    plugin.initiate();
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_RELOAD_CONFIGS").replace("{File}", args[1] + ".file")));
                    return;
                }
                if (sender instanceof Player) {
                    UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.SETTINGS.FILE_NOT_FOUND")), (Player) sender);
                }
                sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_FILE_NOT_FOUND").replace("{File}", args[1] + ".yml")));
            }
        }
    }
}