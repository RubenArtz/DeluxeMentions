package ruben_artz.main.spigot.commands.main.SubCommands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.main.SubCommand;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.Objects;

public class Help extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    public Help() {
        super("help:h", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.SETTINGS.ON_COMMAND")), player);
                player.sendMessage(addColor.addColors("&8« » ============== &e✯ &9&lDeluxe Mentions &e✯ &8============== « »"));
                player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_TIP")));
                player.sendMessage(addColor.addColors("&f"));
                ProjectUtil.sendMessage(player, " &8▪ &f/ms reload &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD"), ClickEvent.Action.RUN_COMMAND, "/ms reload", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_BOX"));
                ProjectUtil.sendMessage(player, " &8▪ &f/ms reload <configuration> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD"), ClickEvent.Action.SUGGEST_COMMAND, "/ms reload config/language", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_BOX"));
                ProjectUtil.sendMessage(player, " &8▪ &f/ms help &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP"), ClickEvent.Action.RUN_COMMAND, "/ms help", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP_BOX"));
                ProjectUtil.sendMessage(player, " &8▪ &f/ms language &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_LANGUAGE"), ClickEvent.Action.RUN_COMMAND, "/ms language", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_LANGUAGE_BOX"));
                ProjectUtil.sendMessage(player, " &8▪ &f/mention toggle &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION"), ClickEvent.Action.SUGGEST_COMMAND, "/mention toggle", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_BOX"));
                ProjectUtil.sendMessage(player, " &8▪ &f/mention toggle <player> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER"), ClickEvent.Action.SUGGEST_COMMAND, "/mention toggle ", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER_BOX"));
            } else {
                sender.sendMessage(addColor.addColors("&8« » ============== &e✯ &9&lDeluxe Mentions &e✯ &8============== « »"));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms reload &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms reload <configuration> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms help &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/mention toggle <player> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER")));
            }
            sender.sendMessage(addColor.addColors("&8================================================="));
        }
    }
}
