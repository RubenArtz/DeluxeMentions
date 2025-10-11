package ruben_artz.mention.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.events.antibot.preventAttacks;
import ruben_artz.mention.launcher.Launcher;
import ruben_artz.mention.util.UtilityFunctions;

public class playerJoin implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    /*
     * Check if the player has activated or deactivated the mention option.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void setMention(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UtilityFunctions.runTask(() -> {
            /*
             * Add data if the player does not exist!
             */
            try {
                if (Launcher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    if (preventAttacks.isAttacking()) return;
                    Launcher.getInstance().getCache().addDatabase(player.getUniqueId());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (Launcher.getInstance().getCache().setBool(player.getUniqueId(), "MENTION")) {
                plugin.getIgnoreMention().remove(player.getUniqueId());
                Launcher.getInstance().getCache().set(player.getUniqueId(), "MENTION", true);
            } else {
                plugin.getIgnoreMention().add(player.getUniqueId());
                Launcher.getInstance().getCache().set(player.getUniqueId(), "MENTION", false);
            }
            if (player.hasPermission("DeluxeMentions.BypassCooldown")) {
                Launcher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", true);
                return;
            }
            Launcher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", false);
        });
    }
}