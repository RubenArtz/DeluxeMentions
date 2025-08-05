package ruben_artz.main.spigot.commands.main.SubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.main.SubCommand;
import ruben_artz.main.spigot.inventory.MSInventory;
import ruben_artz.main.spigot.other.ProjectUtil;

public class Language extends SubCommand {
    private final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public Language() {
        super("language:lang", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ProjectUtil.executeSound(plugin.getFileTranslations().getString("MENTION.OPEN_GUI_SOUND"), player);
                MSInventory inv = new MSInventory();
                inv.getInventory(player);
            }
        }
    }
}
