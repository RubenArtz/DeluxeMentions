package ruben_artz.mention.commands.other;

import ruben_artz.mention.commands.other.SubCommands.Toggle;

public class RegisterCommand extends MainPlayer {

    public RegisterCommand() {
        super("DeluxeMentions.Toggle", 1, new Toggle());
    }
}
