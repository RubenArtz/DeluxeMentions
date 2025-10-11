package ruben_artz.mention.events;

import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.util.NotificationManager;
import ruben_artz.mention.util.UtilityFunctions;
import ruben_artz.mention.util.addColor;

public class updateEvent implements Listener {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void checkPlayer(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Audience audience = plugin.getAudiences(player);

         /*
        Ruben_Artz check update.
         */
        if ("Ruben_Artz".equals(player.getName())) {
            for (int i = 0; i < 5; i++) {
                player.sendMessage("");
            }

            UtilityFunctions.runTaskLater(40, () -> {
                try {
                    String latestVersion = NotificationManager.fetchLatestVersion();
                    if (latestVersion == null) {
                        return;
                    }

                    audience.sendMessage(addColor.setColor(player,
                            "&8« » ==== &e✯ &9&lDeluxe Mentions &e✯ &8==== « »"));
                    audience.sendMessage(addColor.setColor(player,
                            "&f"));
                    audience.sendMessage(addColor.setColor(player,
                            "<dark_gray>•</dark_gray> <hover:show_text:'<white>Última versión:</white> <green>" + latestVersion + "</green>'><white>Versión:</white> <green>" + plugin.getVersion() + "</green></hover>"));
                    audience.sendMessage(addColor.setColor(player,
                            "&f"));
                    audience.sendMessage(addColor.setColor(player,
                            "&8==================================="));
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
                }

            });
        } else {
            player.getUniqueId();
        }

        if (!player.isOp() && !player.hasPermission("*")) {
            return;
        }

        UtilityFunctions.runTaskLater(100L, () -> checkForUpdates(audience));
    }

    private void checkForUpdates(Audience audience) {
        try {
            String latestVersion = NotificationManager.fetchLatestVersion();
            if (latestVersion == null) {
                return;
            }

            String currentVersion = plugin.getDescription().getVersion();

            if (!currentVersion.equals(latestVersion)) {
                audience.sendMessage(addColor.setColor("<green>[" + plugin.getDescription().getName() + "] There is a newer plugin version available:</green> <green><b>" + latestVersion + "</b></green>, <green>you're on:</green> <green><b>" + plugin.getDescription().getVersion() + "</b></green>"));

            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to check for updates: " + e.getMessage());
        }
    }
}