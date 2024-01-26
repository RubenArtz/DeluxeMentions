package ruben_artz.main.spigot.commands.main;

import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class MainCommand implements CommandExecutor, TabCompleter {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String permission;
    private final int reqArgs;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand(String name, int param, SubCommand... subCommands) {
        this.permission = name;
        this.reqArgs = param - 1;
        for (int length = subCommands.length, i = 0; i < length; ++i) {
            this.subCommands.add(subCommands[i]);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] array) {
        if (!sender.hasPermission(permission)) {
            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("PERMS.SD_NO_COMMAND")), (Player) sender);
            List<String> Lines = plugin.getConfig().getStringList("PERMS.NO_PERMISSIONS");
            for (String s : Lines) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("#", "▉").replaceAll("<c>", "©"));
                ((Player) sender).sendTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("PERMS.TITLE"))
                        .replace("{PLAYER}", sender.getName())
                        .replaceAll("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻")),
                        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("PERMS.SUBTITLE"))
                                .replace("!Version", plugin.getVersion()).replaceAll("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻"))
                                .replaceAll("<c>", "©"));
            }
            return false;
        }
        if (reqArgs <= -1) {
            onCommand(sender, array);
            return false;
        }
        if (array.length > reqArgs) {
            try {
                for (SubCommand subCommand : subCommands) {
                    if (subCommand.getNames().contains(array[0].toLowerCase())) {
                        if (!sender.hasPermission(subCommand.getPermission()) || !sender.hasPermission(permission)) {
                            ProjectUtil.executeSound(Objects.requireNonNull(plugin.getConfig().getString("PERMS.SD_NO_COMMAND")), (Player) sender);
                            List<String> Lines = plugin.getConfig().getStringList("PERMS.NO_PERMISSIONS");
                            for (String s : Lines) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("#", "▉").replaceAll("<c>", "©"));
                                ((Player) sender).sendTitle(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("PERMS.TITLE"))
                                        .replace("{PLAYER}", sender.getName())
                                        .replace("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻")),
                                        ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("PERMS.SUBTITLE"))
                                                .replace("!Version", plugin.getVersion()).replaceAll("<exclusive>", "&c&l(╯°□°）╯&f&l︵&7&l ┻━┻"))
                                                .replace("<c>", "©"));
                            }
                            return false;
                        }
                        subCommand.onCommand(subCommand.sender = sender, array);
                        return false;
                    }
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    player.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
                    ProjectUtil.sendMessage(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion()), ClickEvent.Action.RUN_COMMAND, "/ms help", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_VERSION_USING_BOX"));
                    player.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
                } else {
                    sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion())));
                    sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
                }
            } catch (NullPointerException exception) {
                onCommand(sender, array);
            }
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
            ProjectUtil.sendMessage(player, plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion()), ClickEvent.Action.RUN_COMMAND, "/ms help", HoverEvent.Action.SHOW_TEXT, plugin.getFileTranslations().getString("MESSAGES.MESSAGES_VERSION_USING_BOX"));
            player.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
        } else {
            sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGE_VERSION_USING").replace("{Version}", plugin.getVersion())));
            sender.sendMessage(addColor.addColors("&8&m--------------------------------------------------"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (commandSender.hasPermission("DeluxeMentions.Admin")) {
            if (args.length == 1) {
                String partialCommand = args[0];
                commands.add("reload");
                commands.add("rl");
                commands.add("help");
                commands.add("language");
                commands.add("lang");
                commands.add("h");
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            } else if (args.length == 2) {
                String partialCommand = args[1];
                if (args[0].equalsIgnoreCase("reload")) {
                    commands.add("config");
                    commands.add("language");
                    commands.add("groups");
                    StringUtil.copyPartialMatches(partialCommand, commands, completions);
                }
            } else {
                return ImmutableList.of();
            }
            Collections.sort(completions);
        }
        return completions;
    }

    private void onCommand(CommandSender sender, String[] array) {}
}
