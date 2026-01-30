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

package artzstudio.dev.mentions.spigot.events.mention;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.api.MentionEvent;
import artzstudio.dev.mentions.spigot.features.sendActionbar;
import artzstudio.dev.mentions.spigot.features.sendTitles;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class everyone implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void getMention(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        Section section = plugin.getGroups().getSection("MENTION.GROUPS.EVERYONE");
        if (section == null) return;

        String prefix = section.getString("PREFIX", "@everyone");

        if (!sender.hasPermission("DeluxeMentions.Everyone") || !UtilityFunctions.containsIgnoreCase(message, prefix)) {
            return;
        }

        String colorPrefix = section.getString("COLOR_OF_THE_PLAYER_MENTIONED", "&b");
        String colorSuffix = section.getString("COLOR_AFTER_MENTION", "&f");

        String newMessage = message.replace(prefix, colorPrefix + prefix + colorSuffix);

        String finalChat = addColor.toLegacyString(sender, newMessage);
        event.setMessage(finalChat);

        boolean useSound = section.getBoolean("USE_SOUND_NOTIFICATION");
        String soundName = useSound ? section.getString("SOUND_NOTIFICATION") : null;

        boolean useTitle = section.getBoolean("USE_TITLES");
        List<String> titleLines = useTitle ? section.getStringList("SEND_TITLE") : null;
        if (titleLines != null) {
            titleLines.replaceAll(line -> replacePlaceholders(line, sender));
        }

        boolean useActionbar = section.getBoolean("USE_ACTIONBAR_MENTION");
        String actionbarMsg = useActionbar ? replacePlaceholders(section.getString("ACTIONBAR_MENTION"), sender) : null;

        boolean useChatMsg = section.getBoolean("USE_MESSAGE_IN_THE_CHAT");
        List<String> extraChatLines = useChatMsg ? section.getStringList("SEND_MESSAGE_IN_CHAT") : null;
        if (extraChatLines != null) {
            extraChatLines.replaceAll(line -> replacePlaceholders(line, sender));
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            UtilityFunctions.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(new MentionEvent(target, sender)));

            if (useSound && soundName != null) {
                UtilityFunctions.executeSound(soundName, target);
            }

            if (useTitle && titleLines != null) {
                for (String line : titleLines) {
                    String[] parts = line.split("::");
                    if (parts.length >= 2) {
                        sendTitles.sendTitle(target, 1, 10, 1234, parts[0], parts[1]);
                    }
                }
            }

            if (useActionbar && actionbarMsg != null) {
                sendActionbar.sendActionBar(target, actionbarMsg, 200);
            }

            if (useChatMsg && extraChatLines != null) {
                Audience audience = plugin.getAudiences(target);
                for (String line : extraChatLines) {
                    audience.sendMessage(addColor.setColor(target, line));
                }
            }
        }
    }

    private String replacePlaceholders(String text, Player sender) {
        if (text == null) return "";
        return text.replace("{Player}", sender.getName())
                .replace("{Email}", "☚✉☛")
                .replace("{Warning}", "≺⚠≻");
    }
}