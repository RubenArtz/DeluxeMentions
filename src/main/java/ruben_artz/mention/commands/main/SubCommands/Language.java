package ruben_artz.mention.commands.main.SubCommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.commands.main.SubCommand;
import ruben_artz.mention.inventory.MSInventory;
import ruben_artz.mention.util.UtilityFunctions;

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
                UtilityFunctions.executeSound(plugin.getFileTranslations().getString("MENTION.OPEN_GUI_SOUND"), player);
                MSInventory inv = new MSInventory();
                inv.getInventory(player);
            }
        }
    }
}
