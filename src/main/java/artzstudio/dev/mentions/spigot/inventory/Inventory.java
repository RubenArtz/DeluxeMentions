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

package artzstudio.dev.mentions.spigot.inventory;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import com.cryptomorin.xseries.XMaterial;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;
import java.util.Objects;

public class Inventory implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String guiTitle;

    public Inventory() {
        String titleConfig = plugin.getFileTranslations().getString("LANGUAGE.INVENTORY.TITLE");
        this.guiTitle = addColor.addColors(titleConfig != null ? titleConfig : "&8Deluxe Mentions &9Inventory");
    }

    public void openInventory(Player player) {
        org.bukkit.inventory.Inventory inventory = Bukkit.createInventory(null, 45, guiTitle);

        for (int i = 36; i <= 44; i++) {
            UtilityFunctions.addItemGlass(inventory, i);
        }

        Section invSection = plugin.getFileTranslations().getSection("LANGUAGE.INVENTORY");
        if (invSection != null) {
            for (Object keyObj : invSection.getKeys()) {
                String key = keyObj.toString();

                if (key.equals("TITLE")) continue;

                String basePath = "LANGUAGE.INVENTORY." + key;
                int slot = invSection.getInt(key + ".SLOT");

                if (key.equals("BOOK")) {
                    UtilityFunctions.addItems(inventory, XMaterial.BOOK, basePath + ".NAME", basePath + ".LORE", slot);
                } else if (key.equals("CLOSE")) {
                    UtilityFunctions.addItems(inventory, XMaterial.ARROW, basePath + ".NAME", basePath + ".LORE", slot);
                } else {
                    if (invSection.getString(key + ".VALUE") != null) {
                        UtilityFunctions.addItemSkull(inventory, basePath + ".NAME", basePath + ".LORE", basePath + ".VALUE", slot);
                    }
                }
            }
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(guiTitle))) {
            return;
        }

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.get()) {
            return;
        }

        if (!event.getCurrentItem().hasItemMeta()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        Section invSection = plugin.getFileTranslations().getSection("LANGUAGE.INVENTORY");

        int closeSlot = invSection.getInt("CLOSE.SLOT");
        int bookSlot = invSection.getInt("BOOK.SLOT");

        if (clickedSlot == closeSlot) {
            player.closeInventory();
            return;
        }

        if (clickedSlot == bookSlot) {
            player.closeInventory();
            for (String line : plugin.getFileTranslations().getStringList("HELP_TO_TRANSLATE.MESSAGE")) {
                player.sendMessage(addColor.addColors(line));
            }
            UtilityFunctions.executeSound(plugin.getFileTranslations().getString("HELP_TO_TRANSLATE.SOUND"), player);
            return;
        }

        for (Object keyObj : invSection.getKeys()) {
            String key = keyObj.toString();
            if (key.equals("TITLE") || key.equals("BOOK") || key.equals("CLOSE")) continue;

            if (invSection.getInt(key + ".SLOT") == clickedSlot) {
                String langFileName = getLanguageFileName(key);
                if (langFileName != null) {
                    setLanguage(player, langFileName);
                }
                return;
            }
        }
    }

    private String getLanguageFileName(String key) {
        return switch (key) {
            case "ENGLISH" -> "en_US";
            case "SPANISH" -> "es_ES";
            case "SUOMI" -> "fi_FI";
            case "FRENCH" -> "fr_FR";
            case "KOREAN" -> "ko_KR";
            case "THAI" -> "th_TH";
            case "TURKEY" -> "tr_TR";
            case "VIETNAMESE" -> "vi_VN";
            case "CHINESE" -> "zh_CH";
            case "ITALIAN" -> "it_IT";
            case "PORTUGUESE" -> "pt_BR";
            default -> null;
        };
    }

    private void setLanguage(Player player, String language) {
        plugin.getConfigYaml().set("MENTION.LANGUAGE", language);

        try {
            plugin.getConfigYaml().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.SETTINGS.RELOAD_SOUND")), player);
        player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_CHANGE_OF_LANGUAGE").replace("{Language}", language)));
        player.closeInventory();
    }
}