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

package artzstudio.dev.mentions.spigot.commands.main;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class MainCommand implements CommandExecutor, TabCompleter {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String permission;
    private final int reqArgs;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(String name, int param, SubCommand... subCommands) {
        this.permission = name;
        this.reqArgs = param - 1;
        for (int length = subCommands.length, i = 0; i < length; ++i) {
            this.subCommands.add(subCommands[i]);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String @NonNull [] array) {
        if (!sender.hasPermission(permission)) {
            sendNoPermissionMessage(sender);
            return false;
        }

        if (reqArgs <= -1) {
            return false;
        }

        if (array.length > reqArgs) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getNames().contains(array[0].toLowerCase())) {
                    if (!sender.hasPermission(subCommand.getPermission())) {
                        sendNoPermissionMessage(sender);
                        return false;
                    }
                    subCommand.onCommand(subCommand.sender = sender, array);
                    return false;
                }
            }

            sendHelpMessage(sender);
            return false;
        }

        sendHelpMessage(sender);
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (!sender.hasPermission(this.permission)) {
            return ImmutableList.of();
        }

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

    private void sendNoPermissionMessage(CommandSender sender) {
        if (sender instanceof Player) {
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("PERMS.SD_NO_COMMAND")), (Player) sender);
        }

        List<String> lines = plugin.getConfigYaml().getStringList("PERMS.NO_PERMISSIONS");
        for (String s : lines) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("#", "▉").replaceAll("<c>", "©"));
        }

        if (sender instanceof Player player) {
            String titleConfig = plugin.getConfigYaml().getString("PERMS.TITLE");
            String subtitleConfig = plugin.getConfigYaml().getString("PERMS.SUBTITLE");

            if (titleConfig != null && subtitleConfig != null) {
                String title = titleConfig
                        .replace("{PLAYER}", player.getName())
                        .replaceAll("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻");

                String subtitle = subtitleConfig
                        .replace("!Version", plugin.getVersion())
                        .replaceAll("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻")
                        .replaceAll("<c>", "©");

                player.sendTitle(ChatColor.translateAlternateColorCodes('&', title),
                        ChatColor.translateAlternateColorCodes('&', subtitle));
            }
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));

        if (sender instanceof Player player) {
            UtilityFunctions.sendMessage(player,
                    plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion()),
                    ClickEvent.Action.RUN_COMMAND,
                    "/ms help",
                    HoverEvent.Action.SHOW_TEXT,
                    plugin.getFileTranslations().getString("MESSAGES.MESSAGES_VERSION_USING_BOX"));
        } else {
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion())));
        }

        sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
    }
}