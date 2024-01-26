package ruben_artz.main.spigot.events.mention;

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
                        if (ProjectUtil.cooldownAdmin.contains(player.getUniqueId()) && !player.hasPermission("DeluxeMentions.BypassCooldown")) {
                            event.setCancelled(true);
                            /*
                             * API
                             */
                            MentionCooldownEvent mentionCooldownEvent = new MentionCooldownEvent(player, mention);
                            ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionCooldownEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_COOLDOWN"), 100);
                            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getGroups().getString("MENTION.COOLDOWN_SOUND")), player);
                            return;
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
                            ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionMyselfEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NO_MENTION"), 100);
                            continue;
                        }
                        final String prefix = plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.COLOR_OF_THE_PLAYER_MENTIONED");
                        final String suffix = plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR.COLOR_AFTER_MENTION");
                        final String chat = addColor.setColors(mention, message.replace(mention.getName(), prefix + mention.getName() + suffix));
                            /*
                            API
                             */
                        MentionEvent mentionEvent = new MentionEvent(mention, player);
                        ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));
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
                        ProjectUtil.cooldownAdmin.add(player.getUniqueId());
                        event.setMessage(chat);
                        ProjectUtil.syncTaskLater(plugin.getGroups().getString("MENTION.GROUPS.ADMINISTRATOR"),
                                plugin.getGroups().getInt("MENTION.GROUPS.ADMINISTRATOR.COOLDOWN_TIME"), () -> ProjectUtil.cooldownAdmin.remove(player.getUniqueId()));
                        return;
                    } else if (mention.hasPermission(Objects.requireNonNull(plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".PERMISSION")))) {
                        if (ProjectUtil.cooldownGroups.contains(player.getUniqueId()) && !player.hasPermission("DeluxeMentions.BypassCooldown")) {
                            event.setCancelled(true);
                            /*
                             * API
                             */
                            MentionCooldownEvent mentionCooldownEvent = new MentionCooldownEvent(mention, player);
                            ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionCooldownEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_COOLDOWN"), 100);
                            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getGroups().getString("MENTION.COOLDOWN_SOUND")), player);
                            return;
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
                            ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionMyselfEvent));
                            /*
                             * Send Features
                             */
                            sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_NO_MENTION"), 100);
                            continue;
                        }
                        final String prefix = plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".COLOR_OF_THE_PLAYER_MENTIONED");
                        final String suffix = plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".COLOR_AFTER_MENTION");
                        final String chat = addColor.setColors(mention, message.replace(mention.getName(), prefix + mention.getName() + suffix));
                        /*
                          API
                         */
                        MentionEvent mentionEvent = new MentionEvent(mention, player);
                        ProjectUtil.syncRunTask(() -> Bukkit.getServer().getPluginManager().callEvent(mentionEvent));
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
                        event.setMessage(chat);
                        ProjectUtil.cooldownGroups.add(player.getUniqueId());
                        ProjectUtil.syncTaskLater(plugin.getGroups().getString("MENTION.GROUPS.PLAYERS." + key + ".TIME_UNIT"),
                                plugin.getGroups().getInt("MENTION.GROUPS.PLAYERS." + key + ".COOLDOWN_TIME"), () -> ProjectUtil.cooldownGroups.remove(player.getUniqueId()));
                        return;
                    }
                }
            }
        }
    }
}
