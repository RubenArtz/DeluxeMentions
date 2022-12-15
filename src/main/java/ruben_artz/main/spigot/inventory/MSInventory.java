package ruben_artz.main.spigot.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

public class MSInventory implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String title = addColor.addColors("&8Deluxe Mentions &9Inventory");

    public void getInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 45, title);
        /*
        put Glass
        */
        for (int i = 36; i <= 44; i++) {
            ProjectUtil.addItemGlass(inventory, i);
        }
        /*
        put Skulls
         */
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.ENGLISH.NAME", "LANGUAGE.INVENTORY.ENGLISH.LORE", "LANGUAGE.INVENTORY.ENGLISH.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.ENGLISH.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.SPANISH.NAME", "LANGUAGE.INVENTORY.SPANISH.LORE", "LANGUAGE.INVENTORY.SPANISH.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.SPANISH.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.SUOMI.NAME", "LANGUAGE.INVENTORY.SUOMI.LORE", "LANGUAGE.INVENTORY.SUOMI.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.SUOMI.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.FRENCH.NAME", "LANGUAGE.INVENTORY.FRENCH.LORE", "LANGUAGE.INVENTORY.FRENCH.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.FRENCH.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.KOREAN.NAME", "LANGUAGE.INVENTORY.KOREAN.LORE", "LANGUAGE.INVENTORY.KOREAN.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.KOREAN.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.THAI.NAME", "LANGUAGE.INVENTORY.THAI.LORE", "LANGUAGE.INVENTORY.THAI.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.THAI.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.TURKEY.NAME", "LANGUAGE.INVENTORY.TURKEY.LORE", "LANGUAGE.INVENTORY.TURKEY.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.TURKEY.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.VIETNAMESE.NAME", "LANGUAGE.INVENTORY.VIETNAMESE.LORE", "LANGUAGE.INVENTORY.VIETNAMESE.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.VIETNAMESE.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.CHINESE.NAME", "LANGUAGE.INVENTORY.CHINESE.LORE", "LANGUAGE.INVENTORY.CHINESE.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.CHINESE.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.ITALIAN.NAME", "LANGUAGE.INVENTORY.ITALIAN.LORE", "LANGUAGE.INVENTORY.ITALIAN.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.ITALIAN.SLOT"));
        ProjectUtil.addItemSkull(inventory, "LANGUAGE.INVENTORY.PORTUGUESE.NAME", "LANGUAGE.INVENTORY.PORTUGUESE.LORE", "LANGUAGE.INVENTORY.PORTUGUESE.VALUE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.PORTUGUESE.SLOT"));
        /*
        add Items
         */
        ProjectUtil.addItems(inventory, XMaterial.BOOK, "LANGUAGE.INVENTORY.BOOK.NAME", "LANGUAGE.INVENTORY.BOOK.LORE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.BOOK.SLOT"));
        ProjectUtil.addItems(inventory, XMaterial.ARROW, "LANGUAGE.INVENTORY.CLOSE.NAME", "LANGUAGE.INVENTORY.CLOSE.LORE", plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.CLOSE.SLOT"));

        player.openInventory(inventory);
    }

    @EventHandler
    public void getInv(InventoryClickEvent event) {
        if (ChatColor.stripColor(event.getView().getTitle()).equals(ChatColor.stripColor(title))) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == XMaterial.AIR.parseMaterial()) {
                event.setCancelled(true);
            } else if (event.getCurrentItem().hasItemMeta()) {
                Player player = (Player) event.getWhoClicked();
                event.setCancelled(true);
                if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.BOOK.SLOT")) {
                    player.closeInventory();
                    for (String args : plugin.getFileTranslations().getStringList("HELP_TO_TRANSLATE.MESSAGE")) {
                        player.sendMessage(addColor.addColors(args));
                    }
                    XSound.play(player, plugin.getFileTranslations().getString("HELP_TO_TRANSLATE.SOUND"));
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.CLOSE.SLOT")) {
                    player.closeInventory();
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.ENGLISH.SLOT")) {
                    setLanguage(player, "en_US");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.SPANISH.SLOT")) {
                    setLanguage(player, "es_ES");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.SUOMI.SLOT")) {
                    setLanguage(player, "fi_FI");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.FRENCH.SLOT")) {
                    setLanguage(player, "fr_FR");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.KOREAN.SLOT")) {
                    setLanguage(player, "ko_KR");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.THAI.SLOT")) {
                    setLanguage(player, "th_TH");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.TURKEY.SLOT")) {
                    setLanguage(player, "tr_TR");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.VIETNAMESE.SLOT")) {
                    setLanguage(player, "vi_VN");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.CHINESE.SLOT")) {
                    setLanguage(player, "zh_CH");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.ITALIAN.SLOT")) {
                    setLanguage(player, "it_IT");
                } else if (event.getSlot() == plugin.getFileTranslations().getInt("LANGUAGE.INVENTORY.PORTUGUESE.SLOT")) {
                    setLanguage(player, "pt_BR");
                } else {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    private void setLanguage(Player player, String language) {
        plugin.getConfig().set("MENTION.LANGUAGE", language);
        plugin.saveConfig();
        plugin.initiate();
        XSound.play(player, plugin.getConfig().getString("MENTION.SETTINGS.RELOAD_SOUND"));
        player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_CHANGE_OF_LANGUAGE").replace("{Language}", language)));
    }
}