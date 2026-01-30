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

package artzstudio.dev.mentions.spigot.commands.other.SubCommands;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.commands.other.SubCommand;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Ignore extends SubCommand {

    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Ignore() {
        super("ignore", "DeluxeMentions.Ignore");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NOT_USE_CONSOLE")));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.USAGE")));
            return;
        }

        String action = args[1].toLowerCase();

        if (action.equals("list")) {
            UtilityFunctions.runTaskAsynchronously(() -> {
                List<String> blockedUUIDs = Launcher.getInstance().getCache().getBlockedList(player.getUniqueId());

                if (blockedUUIDs.isEmpty()) {
                    player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.LIST_EMPTY")));
                    return;
                }

                List<String> names = new ArrayList<>();
                for (String uuidStr : blockedUUIDs) {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        String name = offlinePlayer.getName();
                        names.add(Objects.requireNonNullElse(name, "Unknown"));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                String list = String.join("&7, &f", names);

                String message = plugin.getFileTranslations().getString("MESSAGES.IGNORE.LIST_FORMAT")
                        .replace("{Current}", String.valueOf(blockedUUIDs.size()))
                        .replace("{Max}", getLimitDisplay(player))
                        .replace("{List}", list);

                player.sendMessage(addColor.addColors(message));
            });
            return;
        }

        if (args.length < 3) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.USAGE")));
            return;
        }

        String targetName = args[2];
        Player onlineTarget = Bukkit.getPlayer(targetName);

        if (onlineTarget == null) {
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_PLAYER_NOT_FOUND")));
            return;
        }

        processIgnore(player, onlineTarget, action);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("list");
            return suggestions;
        }

        if (args.length == 3) {
            String action = args[1].toLowerCase();
            if (action.equals("add") || action.equals("remove")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getName().equals(sender.getName())) {
                        suggestions.add(p.getName());
                    }
                }
                return suggestions;
            }
        }

        return suggestions;
    }

    private void processIgnore(Player player, OfflinePlayer target, String action) {
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.CANNOT_IGNORE_SELF")));
            return;
        }

        Runnable dbTask = () -> {
            if (action.equals("add")) {
                List<String> currentBlocked = Launcher.getInstance().getCache().getBlockedList(player.getUniqueId());
                int limit = getMaxIgnoreLimit(player);

                if (limit != -1 && currentBlocked.size() >= limit) {
                    String msgLimit = plugin.getFileTranslations().getString("MESSAGES.IGNORE.LIMIT_REACHED")
                            .replace("{Limit}", String.valueOf(limit));

                    player.sendMessage(addColor.addColors(msgLimit));
                    player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.UPGRADE_RANK")));
                    return;
                }

                if (Launcher.getInstance().getCache().isBlocked(player.getUniqueId(), target.getUniqueId())) {
                    String msgAlready = plugin.getFileTranslations().getString("MESSAGES.IGNORE.ALREADY_IGNORED")
                            .replace("{Player}", Objects.requireNonNull(target.getName()));
                    player.sendMessage(addColor.addColors(msgAlready));
                    return;
                }

                Launcher.getInstance().getCache().addBlockedPlayer(player.getUniqueId(), target.getUniqueId());

                String msgSuccess = plugin.getFileTranslations().getString("MESSAGES.IGNORE.ADDED")
                        .replace("{Player}", Objects.requireNonNull(target.getName()));
                player.sendMessage(addColor.addColors(msgSuccess));

            } else if (action.equals("remove")) {
                if (!Launcher.getInstance().getCache().isBlocked(player.getUniqueId(), target.getUniqueId())) {
                    String msgNotIgnored = plugin.getFileTranslations().getString("MESSAGES.IGNORE.NOT_IGNORED")
                            .replace("{Player}", Objects.requireNonNull(target.getName()));
                    player.sendMessage(addColor.addColors(msgNotIgnored));
                    return;
                }

                Launcher.getInstance().getCache().removeBlockedPlayer(player.getUniqueId(), target.getUniqueId());

                String msgRemoved = plugin.getFileTranslations().getString("MESSAGES.IGNORE.REMOVED")
                        .replace("{Player}", Objects.requireNonNull(target.getName()));
                player.sendMessage(addColor.addColors(msgRemoved));

            } else {
                player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.UNKNOWN_SUBCOMMAND")));
            }
        };

        if (Bukkit.isPrimaryThread()) {
            UtilityFunctions.runTaskAsynchronously(dbTask);
        } else {
            dbTask.run();
        }
    }

    private int getMaxIgnoreLimit(Player player) {
        if (player.hasPermission("DeluxeMentions.Ignore.Limit.*") || player.isOp()) {
            return -1;
        }

        int maxLimit = 0;
        String permissionPrefix = "DeluxeMentions.Ignore.Limit.";

        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            if (!permissionInfo.getValue()) continue;

            String currentPerm = permissionInfo.getPermission();

            if (currentPerm.toLowerCase().startsWith(permissionPrefix.toLowerCase())) {
                try {
                    String numberPart = currentPerm.substring(permissionPrefix.length());
                    int limit = Integer.parseInt(numberPart);

                    if (limit > maxLimit) {
                        maxLimit = limit;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return maxLimit;
    }

    private String getLimitDisplay(Player player) {
        int limit = getMaxIgnoreLimit(player);
        return limit == -1 ? plugin.getFileTranslations().getString("MESSAGES.IGNORE.INFINITE") : String.valueOf(limit);
    }
}