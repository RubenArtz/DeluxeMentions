package ruben_artz.main.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.events.antibot.preventAttacks;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.ProjectUtil;

public class playerJoin implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    /*
     * Check if the player has activated or deactivated the mention option.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void setMention(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProjectUtil.syncRunTask(() -> {
            /*
             * Add data if the player does not exist!
             */
            try {
                if (MSLauncher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    if (preventAttacks.isAttacking()) return;
                    MSLauncher.getInstance().getCache().addDatabase(player.getUniqueId());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (MSLauncher.getInstance().getCache().setBool(player.getUniqueId(), "MENTION")) {
                plugin.getIgnoreMention().remove(player.getUniqueId());
                MSLauncher.getInstance().getCache().set(player.getUniqueId(), "MENTION", true);
            } else {
                plugin.getIgnoreMention().add(player.getUniqueId());
                MSLauncher.getInstance().getCache().set(player.getUniqueId(), "MENTION", false);
            }
            if (player.hasPermission("DeluxeMentions.BypassCooldown")) {
                MSLauncher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", true);
                return;
            }
            MSLauncher.getInstance().getCache().set(player.getUniqueId(), "EXCLUDETIMER", false);
        });
    }
}