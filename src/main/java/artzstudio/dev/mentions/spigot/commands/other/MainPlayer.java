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

package artzstudio.dev.mentions.spigot.commands.other;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainPlayer implements CommandExecutor, TabCompleter {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final int reqArgs;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainPlayer(int param, SubCommand... subCommands) {
        this.reqArgs = param - 1;
        for (int length = subCommands.length, i = 0; i < length; ++i) {
            this.subCommands.add(subCommands[i]);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] array) {
        if (reqArgs <= -1) {
            onCommand(sender, array);
            return false;
        }
        if (array.length > reqArgs) {
            try {
                for (SubCommand subCommand : subCommands) {
                    if (subCommand.getNames().contains(array[0].toLowerCase())) {
                        if (!sender.hasPermission(subCommand.getPermission())) {
                            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.NO_PERMISSIONS_SOUND")), (Player) sender);
                            sender.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_NO_PERMISSIONS"))));
                            return false;
                        }
                        subCommand.onCommand(subCommand.sender = sender, array);
                        return false;
                    }
                }
                if (sender instanceof Player player) {
                    UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.ARGS_SOUND")), player);
                    player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
                } else {
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
                }
            } catch (NullPointerException exception) {
                onCommand(sender, array);
            }
            return false;
        }
        if (sender instanceof Player player) {
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.ARGS_SOUND")), player);
            player.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders(player, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS"))));
        } else {
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
        }
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands) {
                if (sender.hasPermission(subCommand.getPermission())) {
                    suggestions.addAll(subCommand.getNames());
                }
            }
            StringUtil.copyPartialMatches(args[0], suggestions, completions);
        } else if (args.length > 1) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getNames().contains(args[0].toLowerCase())) {
                    if (sender.hasPermission(subCommand.getPermission())) {
                        suggestions = subCommand.onTabComplete(sender, args);
                    }
                    break;
                }
            }
            if (suggestions != null) {
                StringUtil.copyPartialMatches(args[args.length - 1], suggestions, completions);
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private void onCommand(CommandSender sender, String[] array) {
    }
}