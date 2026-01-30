/*
 *
 *  * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *  *
 *  * This file is part of DeluxeMentions.
 *  *
 *  * DeluxeMentions is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * DeluxeMentions is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.commands.main.SubCommands;

import artzstudio.dev.mentions.spigot.commands.main.SubCommand;
import artzstudio.dev.mentions.spigot.inventory.Inventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Language extends SubCommand {
    public Language() {
        super("language", "DeluxeMentions.Admin");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is only for players.");
            return;
        }


        if (args.length == 1) {
            Inventory inv = new Inventory();
            inv.openInventory(player);
        }
    }
}