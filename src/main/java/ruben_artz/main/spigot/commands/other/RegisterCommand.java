package ruben_artz.main.spigot.commands.other;

import ruben_artz.main.spigot.commands.other.SubCommands.*;

public class RegisterCommand extends MainPlayer {

    public RegisterCommand() {
        super("DeluxeMentions.Toggle", 1, new Toggle());
    }
}
