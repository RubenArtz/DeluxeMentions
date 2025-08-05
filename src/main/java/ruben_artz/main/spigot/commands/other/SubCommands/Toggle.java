package ruben_artz.main.spigot.commands.other.SubCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.other.SubCommand;
import ruben_artz.main.spigot.features.sendActionbar;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.Objects;

public class Toggle extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Toggle() {
        super("toggle:mentionme", "DeluxeMentions.Toggle");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            ProjectUtil.runTask(() -> {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (plugin.IgnoreMention.contains(player.getUniqueId())) {
                        plugin.getIgnoreMention().remove(player.getUniqueId());
                        MSLauncher.getInstance().getCache().set(player.getUniqueId(), "MENTION", true);
                        ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.ACTIVATED_SOUND")), player);
                        sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ACTIVATED"), 80);
                    } else {
                        plugin.getIgnoreMention().add(player.getUniqueId());
                        MSLauncher.getInstance().getCache().set(player.getUniqueId(), "MENTION", false);
                        ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.DISABLED_SOUND")), player);
                        sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_DISABLED"), 80);
                    }
                } else {
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGE.MESSAGE_NOT_USE_CONSOLE")));
                }
            });
            return;
        }
        if (args.length >= 2) {
            if (!sender.hasPermission("DeluxeMentions.Admin")) {
                ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.NO_PERMISSIONS_SOUND")), (Player) sender);
                sender.sendMessage(addColor.addColors(ProjectUtil.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_NO_PERMISSIONS"))));
                return;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if ((target == null) || (!target.getName().equalsIgnoreCase(args[1]))) {
                sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_PLAYER_NOT_FOUND")));
                return;
            }
            ProjectUtil.runTask(() -> {
                if (plugin.IgnoreMention.contains(target.getUniqueId())) {
                    plugin.getIgnoreMention().remove(target.getUniqueId());
                    MSLauncher.getInstance().getCache().set(target.getUniqueId(), "MENTION", true);
                    ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.ACTIVATED_SOUND")), target);
                    sendActionbar.sendActionBar(target, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ACTIVATED"), 80);
                } else {
                    plugin.getIgnoreMention().add(target.getUniqueId());
                    MSLauncher.getInstance().getCache().set(target.getUniqueId(), "MENTION", false);
                    ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.DISABLED_SOUND")), target);
                    sendActionbar.sendActionBar(target, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_DISABLED"), 80);
                }
            });
            ProjectUtil.runTaskLater(10, () -> {
                if (plugin.IgnoreMention.contains(target.getUniqueId())) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_ENABLED_MENTION_TARGET")
                                .replace("{Player}", target.getName()), 120);
                    } else {
                        sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_ENABLED_MENTION_TARGET")
                                .replace("{Player}", target.getName())));
                    }
                } else {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        sendActionbar.sendActionBar(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_DISABLED_MENTION_TARGET")
                                .replace("{Player}", target.getName()), 120);
                    } else {
                        sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_DISABLED_MENTION_TARGET")
                                .replace("{Player}", target.getName())));
                    }
                }
            });
        }
    }
}
