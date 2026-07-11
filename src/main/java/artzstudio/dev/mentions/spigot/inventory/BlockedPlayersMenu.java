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

package artzstudio.dev.mentions.spigot.inventory;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import artzstudio.dev.mentions.spigot.util.addColor;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockedPlayersMenu {

    public static final int PAGE_SIZE = 45;
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    // Converted to ConcurrentHashMaps for thread safety
    @Getter
    private static final Map<UUID, List<PlayerEntry>> blockedPlayersCache = new ConcurrentHashMap<>();
    @Getter
    private static final Map<UUID, Integer> playerPages = new ConcurrentHashMap<>();
    @Getter
    private static final Map<UUID, String> searchFilters = new ConcurrentHashMap<>();

    public static int getPageSize() {
        return PAGE_SIZE;
    }

    /**
     * Builds and opens the main blocked players inventory.
     * This will resolve offline player names based on UUIDs.
     */
    public Inventory createInventory(Player player, List<String> blockedUUIDs) {
        String title = addColor.addColors(getConfigString());
        Inventory inv = Bukkit.createInventory(null, 54, title);

        List<PlayerEntry> entries = new ArrayList<>();

        // Build cached entries with resolved names
        for (String uuidStr : blockedUUIDs) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : plugin.getFileTranslations().getString("MESSAGES.IGNORE.UNKNOWN_PLAYER", "Unknown");
                entries.add(new PlayerEntry(uuid, name));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid UUID strings
            }
        }

        blockedPlayersCache.put(player.getUniqueId(), entries);

        // Apply search filter if there's one active
        String filter = searchFilters.get(player.getUniqueId());
        List<PlayerEntry> filtered = entries;
        if (filter != null && !filter.isEmpty()) {
            filtered = entries.stream()
                    .filter(e -> e.name().toLowerCase().contains(filter.toLowerCase()))
                    .toList();
        }

        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);
        fillInventory(inv, filtered, currentPage, plugin.getInventory().getSection("BLOCKED"));

        return inv;
    }

    /**
     * Builds and opens a filtered version of the blocked players inventory.
     */
    public Inventory createSearchInventory(Player player, String search) {
        searchFilters.put(player.getUniqueId(), search);
        playerPages.put(player.getUniqueId(), 0);

        List<PlayerEntry> allEntries = blockedPlayersCache.getOrDefault(player.getUniqueId(), new ArrayList<>());

        List<PlayerEntry> filtered = allEntries.stream()
                .filter(e -> e.name().toLowerCase().contains(search.toLowerCase()))
                .toList();

        String title = addColor.addColors(getConfigString());
        Inventory inv = Bukkit.createInventory(null, 54, title);

        fillInventory(inv, filtered, 0, plugin.getInventory().getSection("BLOCKED"));

        return inv;
    }

    /**
     * Populates the provided inventory object with player heads and navigation buttons.
     */
    private void fillInventory(Inventory inv, List<PlayerEntry> entries, int currentPage, Section config) {
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, entries.size());

        inv.clear();

        // Populate slots 0-44 with player skulls
        for (int i = startIndex; i < endIndex; i++) {
            PlayerEntry entry = entries.get(i);
            int slot = i - startIndex;
            inv.setItem(slot, getPlayerHead(entry.uuid(), entry.name()));
        }

        addNavigationButtons(inv, entries.size(), currentPage, config);
        addActionButtons(inv, config);

        // Fill empty bottom row slots with aesthetic glass
        for (int i = 45; i < 54; i++) {
            if (inv.getItem(i) == null) {
                UtilityFunctions.addItemGlass(inv, i);
            }
        }
    }

    private ItemStack getPlayerHead(UUID targetUUID, String playerName) {
        Section config = plugin.getInventory().getSection("BLOCKED.PLAYER_HEAD");
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return item;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return item;

        String name = config != null ? config.getString("NAME", "&f{Player}") : "&f{Player}";
        name = name.replace("{Player}", playerName);
        meta.setDisplayName(addColor.addColors(name));

        if (config != null) {
            List<String> lore = config.getStringList("LORE");
            lore = replaceLorePlaceholders(lore, playerName);
            meta.setLore(addColor.addColors(lore));
        }

        // Attempt to apply the player's real skin using XSeries
        try {
            item.setItemMeta(XSkull.of(meta).profile(Profileable.of(targetUUID)).apply());
        } catch (Exception e) {
            item.setItemMeta(meta);
        }

        return item;
    }

    private List<String> replaceLorePlaceholders(List<String> lore, String playerName) {
        return lore.stream()
                .map(line -> line.replace("{Player}", playerName))
                .toList();
    }

    private void addNavigationButtons(Inventory inv, int totalItems, int currentPage, Section config) {
        if (config == null) return;

        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        if (currentPage > 0) {
            int slot = config.getInt("PREVIOUS_PAGE.SLOT", 45);
            String name = config.getString("PREVIOUS_PAGE.NAME", "&a« &7Previous Page");
            List<String> lore = config.getStringList("PREVIOUS_PAGE.LORE");
            inv.setItem(slot, createItem(XMaterial.ARROW, name, lore));
        }

        if (currentPage < totalPages - 1) {
            int slot = config.getInt("NEXT_PAGE.SLOT", 53);
            String name = config.getString("NEXT_PAGE.NAME", "&aNext Page &7»");
            List<String> lore = config.getStringList("NEXT_PAGE.LORE");
            inv.setItem(slot, createItem(XMaterial.ARROW, name, lore));
        }
    }

    private void addActionButtons(Inventory inv, Section config) {
        if (config == null) return;

        // Close Button
        int closeSlot = config.getInt("CLOSE.SLOT", 49);
        inv.setItem(closeSlot, createItem(XMaterial.BARRIER,
                config.getString("CLOSE.NAME", "&c&lClose"),
                config.getStringList("CLOSE.LORE")));

        // Search Button
        int searchSlot = config.getInt("SEARCH_BUTTON.SLOT", 48);
        inv.setItem(searchSlot, createItem(XMaterial.COMPASS,
                config.getString("SEARCH_BUTTON.NAME", "&b&lSearch"),
                config.getStringList("SEARCH_BUTTON.LORE")));

        // Add Button
        int addSlot = config.getInt("ADD_BUTTON.SLOT", 50);
        String addTexture = config.getString("ADD_BUTTON.TEXTURE", "");
        addSkullItem(inv, addSlot, addTexture,
                config.getString("ADD_BUTTON.NAME", "&a&lAdd Player"),
                config.getStringList("ADD_BUTTON.LORE"));
    }

    private ItemStack createItem(XMaterial material, String name, List<String> lore) {
        ItemStack item = new ItemStack(Objects.requireNonNull(material.parseItem()));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(addColor.addColors(name));
            meta.setLore(addColor.addColors(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    private void addSkullItem(Inventory inv, int slot, String texture, String name, List<String> lore) {
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        if (item == null) return;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(addColor.addColors(name));
        meta.setLore(addColor.addColors(lore));

        // Attempt different methods of applying custom skull textures gracefully
        if (texture != null && !texture.isEmpty()) {
            try {
                // Method 1: Try base64
                item.setItemMeta(XSkull.of(meta).profile(Profileable.of(ProfileInputType.BASE64, texture)).apply());
            } catch (Exception e1) {
                try {
                    // Method 2: Try Texture Hash
                    item.setItemMeta(XSkull.of(meta).profile(Profileable.of(ProfileInputType.TEXTURE_HASH, texture)).apply());
                } catch (Exception e2) {
                    // Method 3: Fallback to default UUID
                    try {
                        item.setItemMeta(XSkull.of(meta).profile(Profileable.of(UUID.fromString(UtilityFunctions.DEFAULT_UUID))).apply());
                    } catch (Exception e3) {
                        item.setItemMeta(meta);
                    }
                }
            }
        } else {
            try {
                item.setItemMeta(XSkull.of(meta).profile(Profileable.of(UUID.fromString(UtilityFunctions.DEFAULT_UUID))).apply());
            } catch (Exception e) {
                item.setItemMeta(meta);
            }
        }

        inv.setItem(slot, item);
    }

    private String getConfigString() {
        Section config = plugin.getInventory().getSection("BLOCKED");
        if (config == null) return "&8Blocked Players";
        return config.getString("TITLE", "&8Blocked Players");
    }

    public record PlayerEntry(UUID uuid, String name) {
    }
}