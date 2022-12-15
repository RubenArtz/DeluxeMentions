package ruben_artz.main.spigot.util;

import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.features.sendActionbar;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

@SuppressWarnings("deprecation")
public class MSUpdater implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void Updates(PlayerJoinEvent event) {
        if (this.plugin.getConfig().getBoolean("MENTION.CHECK_UPDATE")) {
            Player player = event.getPlayer();
            ProjectUtil.synTaskAsynchronously(() -> {
                MSLauncher.getInstance().updateChecker(MSLauncher.UPDATER.JOIN_PLAYER);
                if ((player.isOp()) && (!this.plugin.getVersion().equals(this.plugin.getLatestVersion()))) {
                    try {
                        String OLD = addColor.addColors("&fYou have an old version of the plugin. You are");
                        XSound.play(player, plugin.getConfig().getString("MENTION.SETTINGS.NO_UPDATE_SOUND"));
                        player.sendMessage(addColor.addColors(this.plugin.prefix + "" + OLD + ""));
                        player.sendMessage(addColor.addColors("&fusing &e" + plugin.version + "&f, available version &e" + plugin.getLatestVersion() + "&f, &9https://www.spigotmc.org/resources/67248/"));
                        player.sendTitle(addColor.addColors("&8[&9Deluxe Mentions&8]"), addColor.addColors("&fOld version &e" + plugin.version + " &fNew version &e" + plugin.getLatestVersion() + ""));
                        sendActionbar.sendActionBar(player, "&cDownload the latest version &e"+plugin.getLatestVersion()+"", 120);
                    } catch (Exception ignored) {}
                }
            });
        }
    }
}