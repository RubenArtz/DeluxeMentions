/*
 *
 * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *
 * This file is part of DeluxeMentions.
 *
 * DeluxeMentions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DeluxeMentions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.commands.main.SubCommands;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.commands.main.SubCommand;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Help extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Help() {
        super("help", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player player) {
                UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.SETTINGS.ON_COMMAND")), player);
                player.sendMessage(addColor.addColors("&8« » ============== &e✯ &9&lDeluxe Mentions &e✯ &8============== « »"));
                player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_TIP")));
                player.sendMessage(addColor.addColors("&f"));

                UtilityFunctions.sendMessage(player, " &8▪ &f/ms reload &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD"), ClickEvent.Action.RUN_COMMAND, "/ms reload", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_BOX"));
                UtilityFunctions.sendMessage(player, " &8▪ &f/ms reload <configuration> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD"), ClickEvent.Action.SUGGEST_COMMAND, "/ms reload config/language", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD_BOX"));
                UtilityFunctions.sendMessage(player, " &8▪ &f/ms help &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP"), ClickEvent.Action.RUN_COMMAND, "/ms help", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP_BOX"));
                UtilityFunctions.sendMessage(player, " &8▪ &f/ms language &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_LANGUAGE"), ClickEvent.Action.RUN_COMMAND, "/ms language", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_LANGUAGE_BOX"));

                UtilityFunctions.sendMessage(player, " &8▪ &f/mention toggle &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION"), ClickEvent.Action.SUGGEST_COMMAND, "/mention toggle", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_BOX"));
                UtilityFunctions.sendMessage(player, " &8▪ &f/mention toggle <player> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER"), ClickEvent.Action.SUGGEST_COMMAND, "/mention toggle ", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER_BOX"));

                UtilityFunctions.sendMessage(player, " &8▪ &f/mention ignore &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_IGNORE"), ClickEvent.Action.SUGGEST_COMMAND, "/mention ignore ", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_IGNORE_BOX"));

            } else {
                sender.sendMessage(addColor.addColors("&8« » ============== &e✯ &9&lDeluxe Mentions &e✯ &8============== « »"));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms reload &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms reload <configuration> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_RELOAD")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/ms help &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_HELP")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/mention toggle <player> &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_MENTION_PLAYER")));
                sender.sendMessage(addColor.addColors(" &8▪ &f/mention ignore &7» " + plugin.getFileTranslations().getString("MESSAGES.MESSAGE_IGNORE")));
            }
            sender.sendMessage(addColor.addColors("&8================================================="));
        }
    }
}