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
import artzstudio.dev.mentions.spigot.launcher.Launcher;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockedPlayersListener implements Listener {

    // Using Concurrent Collections to ensure thread safety across sync/async tasks
    private static final Set<UUID> awaitingInput = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> awaitingSearch = ConcurrentHashMap.newKeySet();

    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final BlockedPlayersMenu menu;

    public BlockedPlayersListener() {
        this.menu = new BlockedPlayersMenu();
    }

    /**
     * Checks if a player is currently awaiting to input a name to block.
     */
    public static boolean isAwaitingInput(UUID uuid) {
        return awaitingInput.contains(uuid);
    }

    /**
     * Removes the player from all input await lists (search and block).
     */
    public static void removeAwaitingInput(UUID uuid) {
        awaitingInput.remove(uuid);
        awaitingSearch.remove(uuid);
    }

    /**
     * Opens the Blocked Players inventory for the specified player.
     * Operations involving offline player names are run asynchronously.
     */
    public void openInventory(Player player) {
        BlockedPlayersMenu.getPlayerPages().put(player.getUniqueId(), 0);
        BlockedPlayersMenu.getSearchFilters().remove(player.getUniqueId());

        UtilityFunctions.runTaskAsynchronously(() -> {
            List<String> blockedUUIDs = Launcher.getInstance().getCache().getBlockedList(player.getUniqueId());
            Inventory inv = menu.createInventory(player, blockedUUIDs);
            UtilityFunctions.runTask(() -> player.openInventory(inv));
        });
    }

    /**
     * Refreshes the currently opened inventory to display updated data or search results.
     */
    private void refreshInventory(Player player) {
        String searchFilter = BlockedPlayersMenu.getSearchFilters().get(player.getUniqueId());

        if (searchFilter != null && !searchFilter.isEmpty()) {
            Inventory inv = menu.createSearchInventory(player, searchFilter);
            player.openInventory(inv);
        } else {
            List<BlockedPlayersMenu.PlayerEntry> entries = BlockedPlayersMenu.getBlockedPlayersCache()
                    .getOrDefault(player.getUniqueId(), new ArrayList<>());

            List<String> uuids = new ArrayList<>();
            for (BlockedPlayersMenu.PlayerEntry entry : entries) {
                uuids.add(entry.uuid().toString());
            }

            Inventory inv = menu.createInventory(player, uuids);
            player.openInventory(inv);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String title = addColor.addColors(getTitleConfig());

        // Validate inventory title
        if (!ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(title))) {
            return;
        }

        event.setCancelled(true);

        // Validate clicked item
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.get()) {
            return;
        }
        if (!event.getCurrentItem().hasItemMeta()) {
            return;
        }

        int slot = event.getSlot();
        int currentPage = BlockedPlayersMenu.getPlayerPages().getOrDefault(player.getUniqueId(), 0);
        Section config = plugin.getInventory().getSection("BLOCKED");

        if (config == null) return;

        // Bottom row buttons (slots 45-53) are handled separately
        if (slot >= 45 && slot <= 53) {
            handleBottomRowClick(player, slot, currentPage, config);
            return;
        }

        // Player head clicks (slots 0-44) - removes the player from the block list
        if (slot < BlockedPlayersMenu.getPageSize()) {
            handlePlayerHeadClick(player, slot, currentPage);
        }
    }

    private void handleBottomRowClick(Player player, int slot, int currentPage, Section config) {
        int closeSlot = config.getInt("CLOSE.SLOT", 49);
        int prevSlot = config.getInt("PREVIOUS_PAGE.SLOT", 45);
        int nextSlot = config.getInt("NEXT_PAGE.SLOT", 53);
        int searchSlot = config.getInt("SEARCH_BUTTON.SLOT", 48);
        int addSlot = config.getInt("ADD_BUTTON.SLOT", 50);

        if (slot == closeSlot) {
            player.closeInventory();
            return;
        }

        // Handle Previous Page
        if (slot == prevSlot && currentPage > 0) {
            playClickSound(player);
            BlockedPlayersMenu.getPlayerPages().put(player.getUniqueId(), currentPage - 1);
            refreshInventory(player);
            return;
        }

        // Handle Next Page
        if (slot == nextSlot) {
            List<BlockedPlayersMenu.PlayerEntry> entries = BlockedPlayersMenu.getBlockedPlayersCache()
                    .getOrDefault(player.getUniqueId(), new ArrayList<>());
            String filter = BlockedPlayersMenu.getSearchFilters().get(player.getUniqueId());

            long totalPages;
            if (filter != null && !filter.isEmpty()) {
                long filteredCount = entries.stream()
                        .filter(e -> e.name().toLowerCase().contains(filter.toLowerCase()))
                        .count();
                totalPages = (long) Math.ceil((double) filteredCount / BlockedPlayersMenu.getPageSize());
            } else {
                totalPages = (long) Math.ceil((double) entries.size() / BlockedPlayersMenu.getPageSize());
            }
            if (totalPages == 0) totalPages = 1;

            if (currentPage < totalPages - 1) {
                playClickSound(player);
                BlockedPlayersMenu.getPlayerPages().put(player.getUniqueId(), currentPage + 1);
                refreshInventory(player);
            }
            return;
        }

        // Handle Search Button
        if (slot == searchSlot) {
            player.closeInventory();
            awaitingSearch.add(player.getUniqueId());
            player.sendMessage(addColor.addColors(getSearchPrompt()));
            playClickSound(player);
            return;
        }

        // Handle Add Button
        if (slot == addSlot) {
            player.closeInventory();
            awaitingInput.add(player.getUniqueId());
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.PROMPT_ADD")));
            playClickSound(player);
        }
    }

    private void handlePlayerHeadClick(Player player, int slot, int currentPage) {
        List<BlockedPlayersMenu.PlayerEntry> entries = BlockedPlayersMenu.getBlockedPlayersCache()
                .getOrDefault(player.getUniqueId(), new ArrayList<>());

        String filter = BlockedPlayersMenu.getSearchFilters().get(player.getUniqueId());
        List<BlockedPlayersMenu.PlayerEntry> displayEntries = entries;

        if (filter != null && !filter.isEmpty()) {
            displayEntries = entries.stream()
                    .filter(e -> e.name().toLowerCase().contains(filter.toLowerCase()))
                    .toList();
        }

        int realIndex = (currentPage * BlockedPlayersMenu.getPageSize()) + slot;
        if (realIndex >= displayEntries.size()) return;

        BlockedPlayersMenu.PlayerEntry target = displayEntries.get(realIndex);

        // Remove blocked player asynchronously to avoid lagging the main thread
        UtilityFunctions.runTaskAsynchronously(() -> {
            Launcher.getInstance().getCache().removeBlockedPlayer(player.getUniqueId(), target.uuid());

            // Update local cache
            List<BlockedPlayersMenu.PlayerEntry> updated = new ArrayList<>(entries);
            updated.removeIf(e -> e.uuid().equals(target.uuid()));
            BlockedPlayersMenu.getBlockedPlayersCache().put(player.getUniqueId(), updated);

            // Send messages and refresh UI on the main thread
            UtilityFunctions.runTask(() -> {
                String msg = plugin.getFileTranslations().getString("MESSAGES.IGNORE.REMOVED")
                        .replace("{Player}", target.name());
                player.sendMessage(addColor.addColors(msg));
                playClickSound(player);
                refreshInventory(player);
            });
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String title = addColor.addColors(getTitleConfig());
        if (ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(title))) {
            clearPlayerCache(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Prevents memory leaks by clearing cached data when a player disconnects
        UUID uuid = event.getPlayer().getUniqueId();
        clearPlayerCache(uuid);
        removeAwaitingInput(uuid);
    }

    /**
     * Clears menu-specific caching for a given player UUID.
     */
    private void clearPlayerCache(UUID uuid) {
        BlockedPlayersMenu.getPlayerPages().remove(uuid);
        BlockedPlayersMenu.getBlockedPlayersCache().remove(uuid);
        BlockedPlayersMenu.getSearchFilters().remove(uuid);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (awaitingSearch.contains(player.getUniqueId())) {
            event.setCancelled(true);
            awaitingSearch.remove(player.getUniqueId());

            String search = event.getMessage().trim();
            if (search.equalsIgnoreCase("cancel")) {
                player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.SEARCH_CANCELLED", "&cSearch cancelled.")));
                openInventory(player);
                return;
            }

            List<BlockedPlayersMenu.PlayerEntry> entries = BlockedPlayersMenu.getBlockedPlayersCache()
                    .getOrDefault(player.getUniqueId(), new ArrayList<>());
            List<BlockedPlayersMenu.PlayerEntry> matches = entries.stream()
                    .filter(e -> e.name().toLowerCase().contains(search.toLowerCase()))
                    .toList();

            if (matches.isEmpty()) {
                String noResults = plugin.getFileTranslations().getString("MESSAGES.IGNORE.SEARCH_NO_RESULTS", "&cNo players found matching &e{Search}&c.")
                        .replace("{Search}", search);
                player.sendMessage(addColor.addColors(noResults));
                openInventory(player);
                return;
            }

            // Open inventory back on the main thread
            Inventory inv = menu.createSearchInventory(player, search);
            UtilityFunctions.runTask(() -> player.openInventory(inv));
            return;
        }

        if (!awaitingInput.contains(player.getUniqueId())) return;

        event.setCancelled(true);
        awaitingInput.remove(player.getUniqueId());

        String targetName = event.getMessage().trim();

        if (targetName.equalsIgnoreCase("cancel")) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.ADD_CANCELLED", "&cAdd cancelled.")));
            openInventory(player);
            return;
        }

        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_PLAYER_NOT_FOUND")));
            openInventory(player);
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.IGNORE.CANNOT_IGNORE_SELF")));
            openInventory(player);
            return;
        }

        UtilityFunctions.runTaskAsynchronously(() -> {
            if (Launcher.getInstance().getCache().isBlocked(player.getUniqueId(), target.getUniqueId())) {
                String msg = plugin.getFileTranslations().getString("MESSAGES.IGNORE.ALREADY_IGNORED")
                        .replace("{Player}", target.getName());
                player.sendMessage(addColor.addColors(msg));
                UtilityFunctions.runTask(() -> openInventory(player));
                return;
            }

            List<String> currentBlocked = Launcher.getInstance().getCache().getBlockedList(player.getUniqueId());
            int limit = getMaxIgnoreLimit(player);

            if (limit != -1 && currentBlocked.size() >= limit) {
                String msgLimit = plugin.getFileTranslations().getString("MESSAGES.IGNORE.LIMIT_REACHED")
                        .replace("{Limit}", String.valueOf(limit));
                player.sendMessage(addColor.addColors(msgLimit));
                UtilityFunctions.runTask(() -> openInventory(player));
                return;
            }

            Launcher.getInstance().getCache().addBlockedPlayer(player.getUniqueId(), target.getUniqueId());

            String msg = plugin.getFileTranslations().getString("MESSAGES.IGNORE.ADDED")
                    .replace("{Player}", target.getName());
            player.sendMessage(addColor.addColors(msg));

            UtilityFunctions.runTask(() -> openInventory(player));
        });
    }

    /**
     * Determines the maximum number of players the user is allowed to block
     * by parsing permission nodes (DeluxeMentions.Ignore.Limit.X).
     */
    private int getMaxIgnoreLimit(Player player) {
        if (player.hasPermission("DeluxeMentions.Ignore.Limit.*") || player.isOp()) {
            return -1; // Unlimited
        }

        int maxLimit = 0;
        String permissionPrefix = "deluxementions.ignore.limit.";

        for (org.bukkit.permissions.PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            if (!permissionInfo.getValue()) continue;

            String currentPerm = permissionInfo.getPermission().toLowerCase();

            if (currentPerm.startsWith(permissionPrefix)) {
                try {
                    String numberPart = currentPerm.substring(permissionPrefix.length());
                    int limit = Integer.parseInt(numberPart);
                    maxLimit = Math.max(maxLimit, limit);
                } catch (NumberFormatException ignored) {
                    // Ignore improperly formatted permissions
                }
            }
        }
        return maxLimit;
    }

    private String getTitleConfig() {
        Section config = plugin.getInventory().getSection("BLOCKED");
        if (config == null)
            return plugin.getFileTranslations().getString("MESSAGES.IGNORE.INVENTORY_TITLE", "&8Blocked Players");
        return config.getString("TITLE", plugin.getFileTranslations().getString("MESSAGES.IGNORE.INVENTORY_TITLE", "&8Blocked Players"));
    }

    private String getSearchPrompt() {
        String prompt = plugin.getFileTranslations().getString("MESSAGES.IGNORE.SEARCH_PROMPT");
        return prompt != null ? prompt : "&eType a player name or partial name to search in your blocked list.";
    }

    private void playClickSound(Player player) {
        UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfigYaml().getString("MENTION.SETTINGS.RELOAD_SOUND", "UI_BUTTON_CLICK")), player);
    }
}