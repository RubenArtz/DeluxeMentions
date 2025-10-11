package ruben_artz.mention.events.mention;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.api.MentionEvent;
import ruben_artz.mention.features.sendActionbar;
import ruben_artz.mention.features.sendTitles;
import ruben_artz.mention.util.UtilityFunctions;
import ruben_artz.mention.util.addColor;

import java.util.List;
import java.util.Objects;

public class everyone implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void getMention(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (player.hasPermission("DeluxeMentions.Everyone") && UtilityFunctions.containsIgnoreCase(message, plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX"))) {
            for (Player mention : Bukkit.getOnlinePlayers()) {
                final String prefix = plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.COLOR_OF_THE_PLAYER_MENTIONED");
                final String suffix = plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.COLOR_AFTER_MENTION");
                final String chat = addColor.toLegacyString(mention, message.replace(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX")), prefix + plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.PREFIX") + suffix));

                /*
                   API
                */
                MentionEvent mentionEvent = new MentionEvent(mention, player);
                UtilityFunctions.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));

                /*
                 * Send Features
                 */
                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_SOUND_NOTIFICATION")) {
                    UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.SOUND_NOTIFICATION")), mention);
                }

                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_TITLES")) {
                    for (String listTitle : plugin.getGroups().getStringList("MENTION.GROUPS.EVERYONE.SEND_TITLE")) {
                        listTitle = listTitle
                                .replace("{Player}", player.getName())
                                .replace("{Email}", "☚✉☛")
                                .replace("{Warning}", "≺⚠≻");

                        String[] title = listTitle.split("::");
                        sendTitles.sendTitle(mention, 1, 10, 1234, title[0], title[1]);
                    }
                }

                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_ACTIONBAR_MENTION")) {
                    sendActionbar.sendActionBar(mention, Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.EVERYONE.ACTIONBAR_MENTION"))
                            .replace("{Player}", player.getName())
                            .replace("{Email}", "☚✉☛")
                            .replace("{Warning}", "≺⚠≻"), 200);
                }

                if (plugin.getGroups().getBoolean("MENTION.GROUPS.EVERYONE.USE_MESSAGE_IN_THE_CHAT")) {
                    Audience audience = plugin.getAudiences(mention);
                    List<String> stringList = plugin.getGroups().getStringList("MENTION.GROUPS.EVERYONE.SEND_MESSAGE_IN_CHAT");

                    stringList.replaceAll(s -> s
                            .replace("{Player}", player.getName())
                            .replace("{Email}", "☚✉☛")
                            .replace("{Warning}", "≺⚠≻"));

                    for (String messages : stringList) {

                        audience.sendMessage(addColor.setColor(mention, messages));
                    }
                }

                event.setMessage(chat);
            }
        }
    }
}
