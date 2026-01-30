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

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class SubCommand {
    private final String[] names;
    private final String permission;
    protected CommandSender sender;

    public SubCommand(String name, String permission) {
        this.names = name.split(":");
        this.permission = permission;
    }

    public final List<String> getNames() {
        return Arrays.asList(names);
    }

    public final String getPermission() {
        return this.permission;
    }

    public abstract void onCommand(final CommandSender sender, final String[] args);

    public List<String> onTabComplete(final CommandSender sender, final String[] args) {
        return Collections.emptyList();
    }
}