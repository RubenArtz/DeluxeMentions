package ruben_artz.main.spigot.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.addColor;

public class UtilPlayer implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @SuppressWarnings("deprecation")
    @EventHandler
    public void playerAuthor(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        if ("Ruben_Artz".equals(player.getName())) {
            for (int i = 0; i < 10; i++) {
                player.sendMessage("");
            }
            MSLauncher.getInstance().updateChecker(MSLauncher.UPDATER.JOIN_PLAYER);
            player.sendMessage(addColor.addColors("&8« » ============== &e✯ &9&lDeluxe Mentions &e✯ &8============== « »"));
            player.sendMessage(addColor.addColors("&f"));
            TextComponent version = new TextComponent(addColor.addColors("&fVersion: &av" + plugin.version));
            version.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(addColor.addColors("&fÚltima versión del plugin: &a" + plugin.getLatestVersion())).create()));
            player.spigot().sendMessage(version);
            player.sendMessage(addColor.addColors("&fAutor: &a" + plugin.authors));
            TextComponent web = new TextComponent(addColor.addColors("&fWeb: &a" + plugin.web));
            web.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://stn-studios.com/"));
            web.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(addColor.addColors("&fClick para abrir el enlace.")).create()));
            player.spigot().sendMessage(web);
            player.sendMessage(addColor.addColors("&fPlugin: &a" + plugin.prefix));
            player.sendMessage(addColor.addColors("&8================================================="));
        }
    }
}
