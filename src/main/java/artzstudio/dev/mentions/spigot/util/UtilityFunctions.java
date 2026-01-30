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

package artzstudio.dev.mentions.spigot.util;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class UtilityFunctions {

    public static final String DEFAULT_UUID = "3a730223-120a-4b66-8e1f-3e5a5125875c";
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    @Getter
    static public HashMap<UUID, Integer> delayMention;
    @Getter
    static public HashMap<UUID, Integer> delayMentionAdmin;

    public static String setPlaceholders(Player p, String text) {
        if (isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(p, text);
        }
        return text;
    }

    public static Component setPlaceholders(Player player, Component component) {
        if (!isPluginEnabled("PlaceholderAPI")) return component;

        Pattern placeholderPattern = PlaceholderAPI.getPlaceholderPattern();
        TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder().match(placeholderPattern)
                .replacement((matchResult, builder) -> {
                    String placeholder = matchResult.group(0);
                    String replaced = PlaceholderAPI.setPlaceholders(player, placeholder);
                    return Component.text(addColor.addColors(replaced));
                }).build();
        return component.replaceText(textReplacementConfig);
    }

    public static TagResolver papiResolver(Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            final Component componentPlaceholder = LegacyComponentSerializer.legacyAmpersand().deserialize(parsedPlaceholder);

            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    public static void LoadConfigCommand() {
        plugin.getConfigManager().reloadAll();
    }

    public static boolean isPluginEnabled(String args) {
        return Bukkit.getPluginManager().getPlugin(args) != null && Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(args)).isEnabled();
    }

    public static void addItems(Inventory inventory, XMaterial material, String name, String lore, int slot) {
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(material.parseItem()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) itemMeta.setDisplayName(addColor.addColors(plugin.getFileTranslations().getString(name)));
        if (itemMeta != null) itemMeta.setLore(addColor.addColors(plugin.getFileTranslations().getStringList(lore)));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(slot, itemStack);
    }

    public static void addItemSkull(Inventory inventory, String name, String lore, String texture, int slot) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta skullMeta = (SkullMeta) (item != null ? item.getItemMeta() : null);

        if (skullMeta != null)
            skullMeta.setDisplayName(addColor.addColors(plugin.getFileTranslations().getString(name)));

        if (skullMeta != null) skullMeta.setLore(addColor.addColors(plugin.getFileTranslations().getStringList(lore)));

        if (item != null && skullMeta != null) {
            try {
                item.setItemMeta(XSkull.of(skullMeta).profile(Profileable.of(ProfileInputType.TEXTURE_HASH, plugin.getFileTranslations().getString(texture))).apply());
            } catch (Exception exception) {
                item.setItemMeta(XSkull.of(skullMeta).profile(Profileable.of(UUID.fromString(DEFAULT_UUID))).apply());
            }
        }

        inventory.setItem(slot, item);
    }

    public static void addItemGlass(Inventory inventory, int slot) {
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem()));
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) itemMeta.setDisplayName(addColor.addColors(""));
        itemStack.setItemMeta(itemMeta);
        inventory.setItem(slot, itemStack);
    }

    /*
     * Send Json Message
     */
    @SuppressWarnings("deprecation")
    public static void sendMessage(Player player, String textComponent,
                                   ClickEvent.Action clickEvent, String clickValue,
                                   HoverEvent.Action hoverEvent, String hoverValue) {
        TextComponent message = new TextComponent(addColor.addColors(textComponent));
        message.setClickEvent(new ClickEvent(clickEvent, addColor.addColors(clickValue)));
        message.setHoverEvent(new HoverEvent(hoverEvent, new ComponentBuilder(addColor.addColors(hoverValue)).create()));
        player.spigot().sendMessage(message);
    }

    public static void runTask(Runnable runnable) {
        Launcher.getScheduler().runTask(runnable);
    }

    public static void runTaskLater(long delay, Runnable runnable) {
        Launcher.getScheduler().runTaskLater(runnable, delay);
    }

    public static void runTaskAsynchronously(Runnable runnable) {
        Launcher.getScheduler().runTaskAsynchronously(runnable);
    }

    public static void runTaskTimer(long time, Runnable runnable) {
        Launcher.getScheduler().runTaskTimer(runnable, 0L, time);
    }

    /*
     * set Time
     */
    public static int getMath(String type) {
        if (type.equalsIgnoreCase("SECONDS") || (type.equalsIgnoreCase("SECOND"))) {
            return 20;
        } else if (type.equalsIgnoreCase("MINUTES") || (type.equalsIgnoreCase("MINUTE"))) {
            return 1200;
        } else if (type.equalsIgnoreCase("HOURS") || (type.equalsIgnoreCase("HOUR"))) {
            return 72000;
        } else if (type.equalsIgnoreCase("DAYS") || (type.equalsIgnoreCase("DAY"))) {
            return 1728000;
        } else if (type.equalsIgnoreCase("YEARS") || (type.equalsIgnoreCase("YEAR"))) {
            return 630720000;
        } else {
            return 1200;
        }
    }

    public static int convertToSeconds(String timeUnit, int time) {
        return switch (timeUnit.toUpperCase()) {
            case "SECONDS" -> time;
            case "MINUTES" -> time * 60;
            case "HOURS" -> time * 60 * 60;
            case "DAYS" -> time * 60 * 60 * 24;
            case "YEARS" -> time * 60 * 60 * 24 * 365;
            default -> throw new IllegalArgumentException("Invalid time unit: " + timeUnit);
        };
    }

    public static String convertSecondsToHMS(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        String[] type = plugin.getFileTranslations().getString("MESSAGE_TYPE_TIME").split(";;");

        if (hours > 0) {
            return String.format("%02d:%02d:%02d " + type[0], hours, minutes, remainingSeconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d " + type[1], minutes, remainingSeconds);
        } else {
            return String.format("%02d " + type[2], remainingSeconds);
        }
    }

    public static boolean containsIgnoreCase(final String string, final String search) {
        if (string == null || search == null) {
            return false;
        }
        final int length = search.length();
        if (length == 0) {
            return true;
        }
        for (int i = string.length() - length; i >= 0; --i) {
            if (string.regionMatches(true, i, search, 0, length)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    public static boolean ifIsMysql() {
        return Objects.equals(plugin.getConfigYaml().getString("MENTION.DATABASE.ENABLED"), "true");
    }

    public static void executeSound(@Nonnull String path, final Player player) {
        final String string = "ItemsAdder_Sound: ";
        if (path.startsWith(string)) {
            if (path.contains(", ")) {
                final String[] line = path.replace(string, "").split(", ");
                player.playSound(player.getLocation(), line[0], Float.parseFloat(line[1]), Float.parseFloat(line[2]));
                return;
            }
            final String line = path.replace(string, "");

            player.playSound(player.getLocation(), line, 1.0f, 1.0f);
        } else {
            XSound.play(path, soundPlayer -> soundPlayer.forPlayers(player));
        }
    }
}