package ruben_artz.main.spigot.events.mention;

import com.cryptomorin.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.api.MentionEvent;
import ruben_artz.main.spigot.features.sendActionbar;
import ruben_artz.main.spigot.features.sendTitles;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.Objects;

public class everyone implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void getMention(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        for (Player mention : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("DeluxeMentions.Everyone") && ProjectUtil.containsIgnoreCase(message, plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX"))) {
                /*
                 * Not to mention the one who wrote everyone
                 */
                if (mention.equals(player)) {
                    continue;
                }
                final String prefix = plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.COLOR_OF_THE_PLAYER_MENTIONED");
                final String suffix = plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.COLOR_AFTER_MENTION");
                final String chat = addColor.setColors(mention, message.replace(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX")), prefix + plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX") + suffix));
                /*
                   API
                */
                MentionEvent mentionEvent = new MentionEvent(mention, player);
                ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));
                /*
                 * Send Features
                 */
                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_SOUND_NOTIFICATION")) {
                    XSound.play(mention, plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.SOUND_NOTIFICATION"));
                }
                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_TITLES")) {
                    for (String listTitle : plugin.getGroups().getStringList("MENTION.GROUPS.EVERYONE.SEND_TITLE")) {
                        listTitle = listTitle.replace("{Player}", player.getName()).replace("{Email}", "☚✉☛").replace("{Warning}", "≺⚠≻");

                        String[] title = listTitle.split("::");
                        sendTitles.sendTitle(mention, 1, 10, 1234, title[0], title[1]);
                    }
                }
                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_ACTIONBAR_MENTION")) {
                    sendActionbar.sendActionBar(mention, Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.ACTIONBAR_MENTION"))
                            .replace("{Player}", player.getName())
                            .replace("{Email}", "☚✉☛")
                            .replace("{Warning}", "≺⚠≻"), 100);
                }

                event.setMessage(chat);
            }
        }
    }
}
