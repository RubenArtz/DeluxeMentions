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

package artzstudio.dev.mentions.spigot.commands.other.SubCommands;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.commands.other.SubCommand;
import artzstudio.dev.mentions.spigot.features.sendActionbar;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Toggle extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Toggle() {
        super("toggle", "DeluxeMentions.Toggle");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            UtilityFunctions.runTask(() -> {
                if (sender instanceof Player player) {
                    toggleMentionState(player, player);
                } else {
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGE.MESSAGE_NOT_USE_CONSOLE")));
                }
            });
            return;
        }

        if (args.length >= 2) {
            if (!sender.hasPermission("DeluxeMentions.Admin")) {
                UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.NO_PERMISSIONS_SOUND")), (Player) sender);
                sender.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_NO_PERMISSIONS"))));
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if ((target == null) || (!target.getName().equalsIgnoreCase(args[1]))) {
                sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_PLAYER_NOT_FOUND")));
                return;
            }

            UtilityFunctions.runTask(() -> toggleMentionState(target, sender));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            if (sender.hasPermission("DeluxeMentions.Admin")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    suggestions.add(p.getName());
                }
            }
        }

        return suggestions;
    }

    private void toggleMentionState(Player target, CommandSender viewer) {
        boolean currentlyIgnored = plugin.isIgnoring(target.getUniqueId());

        boolean newIgnoreState = !currentlyIgnored;

        plugin.setIgnoring(target.getUniqueId(), newIgnoreState);

        Launcher.getInstance().getCache().set(target.getUniqueId(), "MENTION", !newIgnoreState);

        if (!newIgnoreState) {
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.ACTIVATED_SOUND")), target);
            if (target.equals(viewer)) {
                sendActionbar.sendActionBar(target, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ACTIVATED"), 80);
            } else {
                sendMessageToViewer(viewer, target, "MESSAGES.MESSAGE_ENABLED_MENTION_TARGET");
            }
        } else {
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.DISABLED_SOUND")), target);
            if (target.equals(viewer)) {
                sendActionbar.sendActionBar(target, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_DISABLED"), 80);
            } else {
                sendMessageToViewer(viewer, target, "MESSAGES.MESSAGE_DISABLED_MENTION_TARGET");
            }
        }
    }

    private void sendMessageToViewer(CommandSender viewer, Player target, String path) {
        String msg = plugin.getFileTranslations().getString(path).replace("{Player}", target.getName());
        if (viewer instanceof Player) {
            sendActionbar.sendActionBar((Player) viewer, msg, 120);
        } else {
            viewer.sendMessage(addColor.addColors(msg));
        }
    }
}