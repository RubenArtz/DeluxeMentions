/*
 *
 *  * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *  *
 *  * This file is part of DeluxeMentions.
 *  *
 *  * DeluxeMentions is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * DeluxeMentions is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.commands.main.SubCommands;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.commands.main.SubCommand;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Reload extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Reload() {
        super("reload", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.SETTINGS.RELOAD_SOUND")), (Player) sender);
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
                    UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.SETTINGS.FILE_NOT_FOUND")), (Player) sender);
                }
                sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.CONFIGURATION.MESSAGE_FILE_NOT_FOUND").replace("{File}", args[1] + ".yml")));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            suggestions.add("config");
            suggestions.add("language");
            suggestions.add("groups");
        }

        return suggestions;
    }
}