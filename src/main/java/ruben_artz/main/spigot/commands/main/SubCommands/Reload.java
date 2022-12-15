package ruben_artz.main.spigot.commands.main.SubCommands;

import com.cryptomorin.xseries.XSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.main.SubCommand;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

public class Reload extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    public Reload() {
        super("reload:rl", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                XSound.play((Player) sender, plugin.getConfig().getString("MENTION.SETTINGS.RELOAD_SOUND"));
            }
            ProjectUtil.LoadConfigCommand();
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_SUCCESS")));
            return;
        }
        if (args.length >= 2) {
            if ((args.length == 2) && (args[0].equalsIgnoreCase("reload"))) {
                if (args[1].equalsIgnoreCase("config")) {
                    ProjectUtil.LoadConfigCommand();
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
                    XSound.play((Player) sender, plugin.getConfig().getString("MENTION.SETTINGS.FILE_NOT_FOUND"));
                }
                sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_FILE_NOT_FOUND").replace("{File}", args[1] + ".yml")));
            }
        }
    }
}