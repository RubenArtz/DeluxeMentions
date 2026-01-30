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

package artzstudio.dev.mentions.spigot.events;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.NotificationManager;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class updateEvent implements Listener {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void checkPlayer(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Audience audience = plugin.getAudiences(player);

         /*
        Ruben_Artz check update.
         */
        if ("Ruben_Artz".equals(player.getName())) {
            for (int i = 0; i < 5; i++) {
                player.sendMessage("");
            }

            UtilityFunctions.runTaskLater(40, () -> {
                try {
                    String latestVersion = NotificationManager.fetchLatestVersion();
                    if (latestVersion == null) {
                        return;
                    }

                    audience.sendMessage(addColor.setColor(player,
                            "&8« » ==== &e✯ &9&lDeluxe Mentions &e✯ &8==== « »"));
                    audience.sendMessage(addColor.setColor(player,
                            "&f"));
                    audience.sendMessage(addColor.setColor(player,
                            "<dark_gray>•</dark_gray> <hover:show_text:'<white>Última versión:</white> <green>" + latestVersion + "</green>'><white>Versión:</white> <green>" + plugin.getVersion() + "</green></hover>"));
                    audience.sendMessage(addColor.setColor(player,
                            "&f"));
                    audience.sendMessage(addColor.setColor(player,
                            "&8==================================="));
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
                }

            });
        } else {
            player.getUniqueId();
        }

        if (!player.isOp() && !player.hasPermission("*")) {
            return;
        }

        UtilityFunctions.runTaskLater(100L, () -> checkForUpdates(audience));
    }

    private void checkForUpdates(Audience audience) {
        try {
            String latestVersion = NotificationManager.fetchLatestVersion();
            if (latestVersion == null) {
                return;
            }

            String currentVersion = plugin.getDescription().getVersion();

            if (!currentVersion.equals(latestVersion)) {
                audience.sendMessage(addColor.setColor("<green>[" + plugin.getDescription().getName() + "] There is a newer plugin version available:</green> <green><b>" + latestVersion + "</b></green>, <green>you're on:</green> <green><b>" + plugin.getDescription().getVersion() + "</b></green>"));

            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
        }
    }
}