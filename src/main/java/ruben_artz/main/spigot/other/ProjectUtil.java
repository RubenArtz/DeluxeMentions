package ruben_artz.main.spigot.other;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
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
import ruben_artz.main.spigot.DeluxeMentions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class ProjectUtil {

    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    public static List<UUID> cooldownAdmin = new ArrayList<>();
    public static List<UUID> cooldownGroups = new ArrayList<>();

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
    public static void LoadConfigCommand(){
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.initiate();
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
        ItemStack itemStack = new ItemStack(Objects.requireNonNull(XMaterial.PLAYER_HEAD.parseItem()));
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        if (skullMeta != null) skullMeta.setDisplayName(addColor.addColors(plugin.getFileTranslations().getString(name)));
        if (skullMeta != null) skullMeta.setLore(addColor.addColors(plugin.getFileTranslations().getStringList(lore)));
        if (skullMeta != null) SkullUtils.applySkin(skullMeta, plugin.getFileTranslations().getString(texture));
        itemStack.setItemMeta(skullMeta);
        inventory.setItem(slot, itemStack);
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
    public static void syncRunTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }
    public static void syncTaskLater(long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }
    public static void syncTaskLater(String timeunit, long delay, Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(plugin, runnable, getMath(timeunit) * delay);
    }
    public static void synTaskAsynchronously(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }
    public static void syncRepeatingTask(String timeunit, long time, Runnable runnable) {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, runnable, 0L, getMath(timeunit) * time);
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
        return Objects.equals(plugin.getConfig().getString("MENTION.DATABASE.ENABLED"), "true");
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
