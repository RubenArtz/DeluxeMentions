package ruben_artz.main.spigot.commands.main;

import ruben_artz.main.spigot.commands.main.SubCommands.*;

public class RegisterCommand extends MainCommand {

    public RegisterCommand() {
        super("DeluxeMentions.Admin", 1, new Help(), new Language(), new Reload());
    }
}
