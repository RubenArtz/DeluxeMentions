package ruben_artz.main.spigot.events.mention;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.api.MentionCooldownEvent;
import ruben_artz.main.spigot.api.MentionEvent;
import ruben_artz.main.spigot.api.MentionMyselfEvent;
import ruben_artz.main.spigot.features.sendActionbar;
import ruben_artz.main.spigot.features.sendTitles;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.List;
import java.util.Objects;

public class target implements Listener {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @EventHandler
    public void mentionTarget(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        for (Player mention : Bukkit.getOnlinePlayers()) {
            String name = mention.getName();
            if (!plugin.IgnoreMention.contains(mention.getUniqueId()) && ProjectUtil.containsIgnoreCase(message, name)) {
                for (String key : Objects.requireNonNull(plugin.getGroups().getConfigurationSection("MENTION.GROUPS.PLAYERS")).getKeys(false)) {
                    if (mention.hasPermission("*") && mention.isOp()) {
                        if (ProjectUtil.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                            if (!player.hasPermission("DeluxeMentions.BypassCooldown")) {
                                event.setCancelled(true);
                                Integer delay = ProjectUtil.getDelayMentionAdmin().get(player.getUniqueId());

                                /*
                                 * API
                                 */
                                MentionCooldownEvent mentionCooldownEvent = new MentionCooldownEvent(player, mention, delay, ProjectUtil.convertSecondsToHMS(delay));
                                ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionCooldownEvent));
                                /*
                                 * Send Features
                                 */
                                sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_COOLDOWN")
                                        .replace("{timeToWait}", ProjectUtil.convertSecondsToHMS(delay)), 100);
                                ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.COOLDOWN_SOUND")), player);
                                return;
                            }
                        }

                        /*
                         * Check if the player is hidden from others
                         */
                        if (ProjectUtil.isVanished(mention)) {
                            return;
                        }

                        /*
                         * Check if the player is mentioning him/herself
                         */
                        if (player.equals(mention)) {
                            /*
                             * API
                             */
                            MentionMyselfEvent mentionMyselfEvent = new MentionMyselfEvent(mention);
                            ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionMyselfEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NO_MENTION"), 100);
                            continue;
                        }

                        final String prefix = plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.COLOR_OF_THE_PLAYER_MENTIONED");
                        final String suffix = plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.COLOR_AFTER_MENTION");
                        final String chat = addColor.toLegacyString(mention, message.replace(mention.getName(), prefix + mention.getName() + suffix));
                            /*
                            API
                             */
                        MentionEvent mentionEvent = new MentionEvent(mention, player);
                        ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));

                        /*
                         * Send Features
                         */
                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.ADMINISTRATOR.USE_SOUND_NOTIFICATION")) {
                            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.SOUND_NOTIFICATION")), mention);
                        }

                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.ADMINISTRATOR.USE_TITLES")) {
                            for (String listTitle : plugin.getGroups().getStringList("MENTION.GROUPS.ADMINISTRATOR.SEND_TITLE")) {
                                listTitle = listTitle
                                        .replace("{Player}", player.getName())
                                        .replace("{Email}", "☚✉☛")
                                        .replace("{Warning}", "≺⚠≻");
                                String[] title = listTitle.split("::");
                                sendTitles.sendTitle(mention, 1, 10, 1234, title[0], title[1]);
                            }
                        }
                        sendActionbar.sendActionBar(mention, Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.ACTIONBAR_MENTION"))
                                .replace("{Player}", player.getName())
                                .replace("{Email}", "☚✉☛")
                                .replace("{Warning}", "≺⚠≻"), 100);

                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.ADMINISTRATOR.USE_MESSAGE_IN_THE_CHAT")) {
                            Audience audience = plugin.getAudiences(mention);
                            List<String> stringList = plugin.getGroups().getStringList("MENTION.GROUPS.ADMINISTRATOR.SEND_MESSAGE_IN_CHAT");

                            stringList.replaceAll(s -> s
                                    .replace("{Player}", player.getName())
                                    .replace("{Email}", "☚✉☛")
                                    .replace("{Warning}", "≺⚠≻"));

                            for (String messages : stringList) {
                                audience.sendMessage(addColor.setColor(mention, messages));
                            }
                        }

                        event.setMessage(chat);

                        String timeUnit = plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.TIME_UNIT");
                        int cooldownTime = plugin.getGroups().getInt("MENTION.GROUPS.ADMINISTRATOR.COOLDOWN_TIME");
                        int delayInSeconds = ProjectUtil.convertToSeconds(Objects.requireNonNull(timeUnit), cooldownTime);

                        if (!ProjectUtil.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                            ProjectUtil.getDelayMentionAdmin().put(player.getUniqueId(), delayInSeconds);
                        }
                        return;
                    } else if (mention.hasPermission(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".PERMISSION")))) {
                        if (ProjectUtil.getDelayMention().containsKey(player.getUniqueId())) {
                            if (!player.hasPermission("DeluxeMentions.BypassCooldown")) {
                                event.setCancelled(true);
                                Integer delay = ProjectUtil.getDelayMention().get(player.getUniqueId());

                                /*
                                 * API
                                 */
                                MentionCooldownEvent mentionCooldownEvent = new MentionCooldownEvent(player, mention, delay, ProjectUtil.convertSecondsToHMS(delay));
                                ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionCooldownEvent));
                                /*
                                 * Send Features
                                 */

                                sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_COOLDOWN")
                                        .replace("{timeToWait}", ProjectUtil.convertSecondsToHMS(delay)), 120);
                                ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.COOLDOWN_SOUND")), player);
                                return;
                            }
                        }

                        /*
                         * Check if the player is hidden from others
                         */
                        if (ProjectUtil.isVanished(mention)) {
                            return;
                        }

                        /*
                         * Check if the player is mentioning him/herself
                         */
                        if (player.equals(mention)) {
                            /*
                             * API
                             */
                            MentionMyselfEvent mentionMyselfEvent = new MentionMyselfEvent(mention);
                            ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionMyselfEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NO_MENTION"), 100);
                            continue;
                        }

                        final String prefix = plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".COLOR_OF_THE_PLAYER_MENTIONED");
                        final String suffix = plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".COLOR_AFTER_MENTION");
                        final String chat = addColor.toLegacyString(mention, message.replace(mention.getName(), prefix + mention.getName() + suffix));
                        /*
                          API
                         */
                        MentionEvent mentionEvent = new MentionEvent(mention, player);
                        ProjectUtil.runTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));
                        /*
                         * Send Features
                         */
                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.PLAYERS." + key + ".USE_SOUND_NOTIFICATION")) {
                            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".SOUND_NOTIFICATION")), mention);
                        }

                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.PLAYERS." + key + ".USE_TITLES")) {
                            for (String listTitle : plugin.getGroups().getStringList("MENTION.GROUPS.PLAYERS." + key + ".SEND_TITLE")) {
                                listTitle = listTitle
                                        .replace("{Player}", player.getName())
                                        .replace("{Email}", "☚✉☛")
                                        .replace("{Warning}", "≺⚠≻");
                                String[] title = listTitle.split("::");
                                sendTitles.sendTitle(mention, 1, 10, 1234, title[0], title[1]);
                            }
                        }

                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.PLAYERS." + key + ".USE_ACTIONBAR_MENTION")) {
                            sendActionbar.sendActionBar(mention, Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".ACTIONBAR_MENTION"))
                                    .replace("{Player}", player.getName())
                                    .replace("{Email}", "☚✉☛")
                                    .replace("{Warning}", "≺⚠≻"), 100);
                        }

                        if (plugin.getGroups().getBoolean("MENTION.GROUPS.PLAYERS." + key + ".USE_MESSAGE_IN_THE_CHAT")) {
                            Audience audience = plugin.getAudiences(mention);
                            List<String> stringList = plugin.getGroups().getStringList("MENTION.GROUPS.PLAYERS." + key + ".SEND_MESSAGE_IN_CHAT");

                            stringList.replaceAll(s -> s
                                    .replace("{Player}", player.getName())
                                    .replace("{Email}", "☚✉☛")
                                    .replace("{Warning}", "≺⚠≻"));

                            for (String messages : stringList) {
                                audience.sendMessage(addColor.setColor(mention, messages));
                            }
                        }

                        event.setMessage(chat);

                        String timeUnit = plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".TIME_UNIT");
                        int cooldownTime = plugin.getGroups().getInt("MENTION.GROUPS.PLAYERS." + key + ".COOLDOWN_TIME");
                        int delayInSeconds = ProjectUtil.convertToSeconds(Objects.requireNonNull(timeUnit), cooldownTime);

                        if (!ProjectUtil.getDelayMention().containsKey(player.getUniqueId())) {
                            ProjectUtil.getDelayMention().put(player.getUniqueId(), delayInSeconds);
                        }
                        return;
                    }
                }
            }
        }
    }
}
