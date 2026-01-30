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

package artzstudio.dev.mentions.spigot.placeholder;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.events.antibot.preventAttacks;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Placeholder extends PlaceholderExpansion implements Configurable {
    public DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    String bypass_true = getString("bypass.true", "&a✔");
    String bypass_false = getString("bypass.false", "&c✖");

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mentions";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "bypass": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (Launcher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (Launcher.getInstance().getCache().setBool(player.getUniqueId(), "EXCLUDETIMER")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
            case "status": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (Launcher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (Launcher.getInstance().getCache().setBool(player.getUniqueId(), "MENTION")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
            case "delayInSeconds": {
                if (preventAttacks.isAttacking()) return "Loading...";

                if (UtilityFunctions.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                    return String.valueOf(UtilityFunctions.getDelayMentionAdmin().get(player.getUniqueId()));
                } else if (UtilityFunctions.getDelayMention().containsKey(player.getUniqueId())) {
                    return String.valueOf(UtilityFunctions.getDelayMention().get(player.getUniqueId()));
                } else {
                    return "0";
                }
            }
            case "delayInFormat": {
                if (preventAttacks.isAttacking()) return "Loading...";

                if (UtilityFunctions.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                    return UtilityFunctions.convertSecondsToHMS(UtilityFunctions.getDelayMentionAdmin().get(player.getUniqueId()));
                } else if (UtilityFunctions.getDelayMention().containsKey(player.getUniqueId())) {
                    return UtilityFunctions.convertSecondsToHMS(UtilityFunctions.getDelayMention().get(player.getUniqueId()));
                } else {
                    return "0";
                }
            }
        }
        return super.onRequest(player, params);
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("bypass.true", "&aYes ✔");
        defaults.put("bypass.false", "&cNo ✖");
        return defaults;
    }
}