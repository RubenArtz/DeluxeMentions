package ruben_artz.main.spigot.commands.main;

import ruben_artz.main.spigot.commands.main.SubCommands.Help;
import ruben_artz.main.spigot.commands.main.SubCommands.Language;
import ruben_artz.main.spigot.commands.main.SubCommands.Reload;

public class RegisterCommand extends MainCommand {

    public RegisterCommand() {
        super("DeluxeMentions.Admin", 1, new Help(), new Language(), new Reload());
    }
}
