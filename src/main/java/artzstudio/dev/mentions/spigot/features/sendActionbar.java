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

package artzstudio.dev.mentions.spigot.features;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import com.github.Anon8281.universalScheduler.bukkitScheduler.BukkitScheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class sendActionbar {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void sendActionBar(Player player, String message) {
        Audience audience = plugin.getAudiences(player);

        message = Objects.requireNonNull(message)
                .replace("{Player}", player.getName())
                .replace("{Address}", String.valueOf(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
                .replace("{Uuid}", player.getUniqueId().toString());
        Component text = addColor.setColor(player, message);

        UtilityFunctions.runTask(() -> audience.sendActionBar(text));
    }

    public static void sendActionBar(Player player, String message, long duration) {
        BukkitScheduler bukkitScheduler = new BukkitScheduler(plugin);

        AtomicLong repeater = new AtomicLong(duration);

        bukkitScheduler.runTaskTimer(() -> {

            sendActionBar(player, message);
            repeater.addAndGet(-40L);
            if (repeater.get() < 0L) bukkitScheduler.cancelTasks();

        }, 0L, 40L);
    }
}