package ruben_artz.mention.commands.other;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.util.UtilityFunctions;
import ruben_artz.mention.util.addColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainPlayer implements CommandExecutor, TabCompleter {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    private final String permission;
    private final int reqArgs;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainPlayer(String name, int param, SubCommand... subCommands) {
        this.permission = name;
        this.reqArgs = param - 1;
        for (int length = subCommands.length, i = 0; i < length; ++i) {
            this.subCommands.add(subCommands[i]);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] array) {
        if (!sender.hasPermission(permission)) {
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.NO_PERMISSIONS_SOUND")), (Player) sender);
            sender.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_NO_PERMISSIONS"))));
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
                            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.NO_PERMISSIONS_SOUND")), (Player) sender);
                            sender.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_NO_PERMISSIONS"))));
                            return false;
                        }
                        subCommand.onCommand(subCommand.sender = sender, array);
                        return false;
                    }
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.ARGS_SOUND")), player);
                    player.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
                } else {
                    sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
                }
            } catch (NullPointerException exception) {
                onCommand(sender, array);
            }
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UtilityFunctions.executeSound(Objects.requireNonNull(plugin.getConfig().getString("MENTION.ARGS_SOUND")), player);
            player.sendMessage(addColor.addColors(UtilityFunctions.setPlaceholders((Player) sender, this.plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS"))));
        } else {
            sender.sendMessage(addColor.addColors(plugin.getFileTranslations().getString("MESSAGES.MESSAGES_ARGS")));
        }
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (!sender.hasPermission("DeluxeMentions.Toggle")) {
                return null;
            }
            String partialCommand = args[0];
            commands.add("toggle");
            commands.add("mentionme");
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else if (args.length == 2) {
            if (!sender.hasPermission("DeluxeMentions.Admin")) {
                return null;
            }
            String partialCommand = args[1];
            if (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("mentionme")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    commands.add(player.getName());
                }
            }
            StringUtil.copyPartialMatches(partialCommand, commands, completions);
        } else {
            return ImmutableList.of();
        }
        Collections.sort(completions);
        return completions;
    }

    private void onCommand(CommandSender sender, String[] array) {
    }
}
