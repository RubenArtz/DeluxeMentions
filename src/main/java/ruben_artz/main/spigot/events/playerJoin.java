package ruben_artz.main.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.Module;
import ruben_artz.main.spigot.events.antibot.preventAttacks;
import ruben_artz.main.spigot.other.ProjectUtil;

public class playerJoin implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    /*
     * Check if the player has activated or deactivated the mention option.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void setMention(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProjectUtil.synTaskAsynchronously(() -> {
            /*
             * Add data if the player does not exist!
             */
            try {
                if (Module.ifNotExists(player.getUniqueId())) {
                    if (preventAttacks.isAttacking()) return;
                    Module.setData(player.getUniqueId());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (Module.getBool(player.getUniqueId(), "MENTION")) {
                plugin.getIgnoreMention().remove(player.getUniqueId());
                Module.set(player.getUniqueId(), "MENTION", true);
            } else {
                plugin.getIgnoreMention().add(player.getUniqueId());
                Module.set(player.getUniqueId(), "MENTION", false);
            }
            if (player.hasPermission("DeluxeMentions.BypassCooldown")) {
                Module.set(player.getUniqueId(), "EXCLUDETIMER", true);
                return;
            }
            Module.set(player.getUniqueId(), "EXCLUDETIMER", false);
        });
    }
}
