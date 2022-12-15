package ruben_artz.main.spigot.commands.main.SubCommands;

import com.cryptomorin.xseries.XSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.main.SubCommand;
import ruben_artz.main.spigot.inventory.MSInventory;

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
                XSound.play(player, plugin.getFileTranslations().getString("MENTION.OPEN_GUI_SOUND"));
                MSInventory inv = new MSInventory();
                inv.getInventory(player);
            }
        }
    }
}
