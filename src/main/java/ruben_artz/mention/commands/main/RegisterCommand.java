package ruben_artz.mention.commands.main;

import ruben_artz.mention.commands.main.SubCommands.Help;
import ruben_artz.mention.commands.main.SubCommands.Language;
import ruben_artz.mention.commands.main.SubCommands.Reload;

public class RegisterCommand extends MainCommand {

    public RegisterCommand() {
        super("DeluxeMentions.Admin", 1, new Help(), new Language(), new Reload());
    }
}
