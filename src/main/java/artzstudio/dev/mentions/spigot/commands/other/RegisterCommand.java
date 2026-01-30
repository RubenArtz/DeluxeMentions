/*
 *
 * Copyright (c) 2026 Ruben_Artz and Artz Studio.
 *
 * This file is part of DeluxeMentions.
 *
 * DeluxeMentions is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DeluxeMentions is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DeluxeMentions.  If not, see https://www.gnu.org/licenses/.
 *
 */

package artzstudio.dev.mentions.spigot.commands.other;

import artzstudio.dev.mentions.spigot.commands.other.SubCommands.Ignore;
import artzstudio.dev.mentions.spigot.commands.other.SubCommands.Toggle;

public class RegisterCommand extends MainPlayer {

    public RegisterCommand() {
        super(1,
                new Toggle(),
                new Ignore());
    }
}