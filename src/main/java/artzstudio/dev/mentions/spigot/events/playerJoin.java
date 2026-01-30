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

package artzstudio.dev.mentions.spigot.events;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.events.antibot.preventAttacks;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class playerJoin implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void setMention(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UtilityFunctions.runTask(() -> {
            try {
                if (Launcher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    if (preventAttacks.isAttacking()) return;
                    Launcher.getInstance().getCache().addDatabase(player.getUniqueId());
                }

                Launcher.getInstance().getCache().loadCache(player.getUniqueId());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            boolean mentionsEnabled = Launcher.getInstance().getCache().setBool(player.getUniqueId(), "MENTION");

            plugin.setIgnoring(player.getUniqueId(), !mentionsEnabled);

            Launcher.getInstance().getCache().set(player.getUniqueId(), "MENTION", mentionsEnabled);

            if (player.hasPermission("DeluxeMentions.BypassCooldown")) {
                Launcher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", true);
                return;
            }
            Launcher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", false);
        });
    }
}