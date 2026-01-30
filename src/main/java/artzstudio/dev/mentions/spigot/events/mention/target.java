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
import artzstudio.dev.mentions.spigot.api.MentionCooldownEvent;
import artzstudio.dev.mentions.spigot.api.MentionEvent;
import artzstudio.dev.mentions.spigot.api.MentionMyselfEvent;
import artzstudio.dev.mentions.spigot.features.sendActionbar;
import artzstudio.dev.mentions.spigot.features.sendTitles;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class target implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void mentionTarget(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (plugin.isIgnoring(target.getUniqueId()) || UtilityFunctions.isVanished(target)) {
                continue;
            }

            boolean isBlocked = Launcher.getInstance().getCache().isBlockedLocally(target.getUniqueId(), player.getUniqueId());
            boolean hasBypass = player.hasPermission("DeluxeMentions.Admin") || player.isOp();

            if (isBlocked && !hasBypass) {
                continue;
            }

            Section groupConfig = getGroupConfig(target);
            if (groupConfig == null) continue;

            boolean requireAt = groupConfig.getBoolean("REQUIRE_AT_SYMBOL", false);
            String searchWord = requireAt ? "@" + target.getName() : target.getName();

            if (!containsWord(message, searchWord)) {
                continue;
            }

            if (player.equals(target)) {
                handleSelfMention(player);
                continue;
            }

            processMentionLogic(event, player, target, groupConfig);
        }
    }

    private Section getGroupConfig(Player target) {
        Section groupsSection = plugin.getGroups().getSection("MENTION.GROUPS.PLAYERS");

        if (target.hasPermission("*") && target.isOp()) {
            return plugin.getGroups().getSection("MENTION.GROUPS.ADMINISTRATOR");
        } else if (groupsSection != null) {
            for (Object keyObj : groupsSection.getKeys()) {
                String key = keyObj.toString();
                String permission = groupsSection.getString(key + ".PERMISSION");
                if (permission != null && target.hasPermission(permission)) {
                    return groupsSection.getSection(key);
                }
            }
        }
        return null;
    }

    private void processMentionLogic(AsyncPlayerChatEvent event, Player sender, Player target, Section groupConfig) {
        String groupKey = "ADMINISTRATOR".equals(groupConfig.getName()) ? "ADMIN" : "PLAYER";

        Map<UUID, Integer> cooldownMap = groupKey.equals("ADMIN")
                ? UtilityFunctions.getDelayMentionAdmin()
                : UtilityFunctions.getDelayMention();

        if (checkCooldown(sender, cooldownMap)) {
            event.setCancelled(true);
            return;
        }

        applyMentionEffects(event, sender, target, groupConfig);

        String timeUnit = groupConfig.getString("TIME_UNIT", "SECONDS");
        int cooldownTime = groupConfig.getInt("COOLDOWN_TIME", 5);
        int delayInSeconds = UtilityFunctions.convertToSeconds(timeUnit, cooldownTime);

        if (!cooldownMap.containsKey(sender.getUniqueId())) {
            cooldownMap.put(sender.getUniqueId(), delayInSeconds);
        }
    }

    private void applyMentionEffects(AsyncPlayerChatEvent event, Player sender, Player target, Section config) {
        String prefix = config.getString("COLOR_OF_THE_PLAYER_MENTIONED", "&a");
        String suffix = config.getString("COLOR_AFTER_MENTION", "&f");

        boolean requireAt = config.getBoolean("REQUIRE_AT_SYMBOL", false);
        String targetString = requireAt ? "@" + target.getName() : target.getName();

        String newMessage = event.getMessage().replace(targetString, prefix + targetString + suffix);
        event.setMessage(addColor.toLegacyString(target, newMessage));

        UtilityFunctions.runTask(() -> Bukkit.getPluginManager().callEvent(new MentionEvent(target, sender)));

        if (config.getBoolean("USE_SOUND_NOTIFICATION")) {
            UtilityFunctions.executeSound(config.getString("SOUND_NOTIFICATION"), target);
        }

        if (config.getBoolean("USE_TITLES")) {
            for (String line : config.getStringList("SEND_TITLE")) {
                line = replacePlaceholders(line, sender);
                String[] parts = line.split("::");
                if (parts.length >= 2) {
                    sendTitles.sendTitle(target, 1, 10, 20, parts[0], parts[1]);
                }
            }
        }

        if (config.getBoolean("USE_ACTIONBAR_MENTION") || config.getString("ACTIONBAR_MENTION") != null) {
            String barMsg = config.getString("ACTIONBAR_MENTION");
            if (barMsg != null) {
                sendActionbar.sendActionBar(target, replacePlaceholders(barMsg, sender), 100);
            }
        }

        if (config.getBoolean("USE_MESSAGE_IN_THE_CHAT")) {
            Audience audience = plugin.getAudiences(target);
            for (String msg : config.getStringList("SEND_MESSAGE_IN_CHAT")) {
                audience.sendMessage(addColor.setColor(target, replacePlaceholders(msg, sender)));
            }
        }
    }

    private boolean checkCooldown(Player sender, Map<UUID, Integer> cooldownMap) {
        if (cooldownMap.containsKey(sender.getUniqueId())) {
            if (!sender.hasPermission("DeluxeMentions.BypassCooldown")) {
                int delay = cooldownMap.get(sender.getUniqueId());

                UtilityFunctions.runTask(() -> Bukkit.getPluginManager().callEvent(new MentionCooldownEvent(sender, null, delay, UtilityFunctions.convertSecondsToHMS(delay))));

                String msg = plugin.getFileTranslations().getString("MESSAGES.MESSAGE_COOLDOWN")
                        .replace("{timeToWait}", UtilityFunctions.convertSecondsToHMS(delay));
                sendActionbar.sendActionBar(sender, msg, 100);
                UtilityFunctions.executeSound(plugin.getConfigYaml().getString("MENTION.COOLDOWN_SOUND"), sender);

                return true;
            }
        }
        return false;
    }

    private void handleSelfMention(Player player) {
        UtilityFunctions.runTask(() -> Bukkit.getPluginManager().callEvent(new MentionMyselfEvent(player)));
        sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NO_MENTION"), 100);
    }

    private String replacePlaceholders(String text, Player sender) {
        return text.replace("{Player}", sender.getName())
                .replace("{Email}", "☚✉☛")
                .replace("{Warning}", "≺⚠≻");
    }

    private boolean containsWord(String message, String word) {
        String patternString;

        if (word.startsWith("@")) {
            patternString = "(?i)(?<=^|\\s)" + Pattern.quote(word) + "\\b";
        } else {
            patternString = "(?i)\\b" + Pattern.quote(word) + "\\b";
        }

        return Pattern.compile(patternString).matcher(message).find();
    }
}