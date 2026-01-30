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

package artzstudio.dev.mentions.spigot.config;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class YamlConfig {

    private final DeluxeMentions plugin;
    private final ConfigType type;

    @Getter
    private YamlDocument document;

    public YamlConfig(DeluxeMentions plugin, ConfigType type) {
        this.plugin = plugin;
        this.type = type;
        load();
    }

    private void load() {
        try {
            String subPath = type.getSubFolder();
            boolean hasSubFolder = subPath != null && !subPath.isEmpty();

            File folder = plugin.getDataFolder();

            if (hasSubFolder) {
                folder = new File(folder, subPath);
            }

            if (!folder.exists() && !folder.mkdirs()) {
                plugin.sendConsole(plugin.getPrefix() + "&cCould not create directory: " + folder.getPath());
            }

            File file = new File(folder, type.getFileName());
            boolean isNew = !file.exists();
            int diskVersion = -1;

            if (file.exists() && type.getVersionRoute() != null) {
                prepareForMigration(file);

                YamlConfiguration temp = YamlConfiguration.loadConfiguration(file);
                diskVersion = temp.getInt(type.getVersionRoute(), -1);
            }

            String resourcePath = hasSubFolder
                    ? subPath + "/" + type.getFileName()
                    : type.getFileName();

            UpdaterSettings.Builder updaterBuilder = UpdaterSettings.builder();
            if (type.getVersionRoute() != null) {
                updaterBuilder.setVersioning(new BasicVersioning(type.getVersionRoute()));
            } else {
                updaterBuilder.setKeepAll(true);
            }

            this.document = YamlDocument.create(
                    file,
                    Objects.requireNonNull(plugin.getResource(resourcePath), "Resource not found in JAR: " + resourcePath),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    updaterBuilder.build()
            );

            if (isNew) {
                plugin.sendConsole(plugin.getPrefix() + "Creating &a" + type.getFileName() + " &fsince it does not exist...");
            } else if (type.getVersionRoute() != null) {
                int newVersion = this.document.getInt(type.getVersionRoute());
                if (newVersion > diskVersion && diskVersion != -1) {
                    plugin.sendConsole(plugin.getPrefix() + "Updating &a" + type.getFileName() + " &ffrom version &e" + diskVersion + " &fto &e" + newVersion + "&f...");
                }
            }

        } catch (IOException e) {
            plugin.sendConsole(plugin.getPrefix() + "&cError loading configuration file: &f" + type.getFileName());
            throw new RuntimeException("Failed to load config: " + type.getFileName(), e);
        }
    }

    private void prepareForMigration(File file) {
        YamlConfiguration tempConfig = YamlConfiguration.loadConfiguration(file);
        String versionRoute = type.getVersionRoute();
        boolean modified = false;

        if (!tempConfig.contains(versionRoute)) {
            plugin.sendConsole(plugin.getPrefix() + "&e[Auto-Fix] &fVersion path missing in &a" + type.getFileName() + "&f. Adding default version...");
            tempConfig.set(versionRoute, 1);
            modified = true;
        } else if (!tempConfig.isInt(versionRoute)) {
            String rawValue = tempConfig.getString(versionRoute);
            try {
                double valDouble = Double.parseDouble(Objects.requireNonNull(rawValue));
                int valInt = (int) valDouble;

                if (valInt > 0) {
                    if (valDouble != valInt) {
                        plugin.sendConsole(plugin.getPrefix() + "&e[Auto-Fix] &fMigrating legacy version format (" + rawValue + ") in &a" + type.getFileName() + "&f...");
                    }
                    tempConfig.set(versionRoute, valInt);
                } else {
                    tempConfig.set(versionRoute, 1);
                }
                modified = true;
            } catch (NumberFormatException e) {
                plugin.sendConsole(plugin.getPrefix() + "&e[Auto-Fix] &fInvalid version format (" + rawValue + ") in &a" + type.getFileName() + "&f. Resetting...");
                tempConfig.set(versionRoute, 1);
                modified = true;
            }
        }

        if (modified) {
            try {
                tempConfig.save(file);
            } catch (IOException e) {
                plugin.sendConsole(plugin.getPrefix() + "&cError saving migration fix for: &f" + type.getFileName());
            }
        }
    }

    public void reload() {
        try {
            if (document != null) document.reload();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to reload " + type.getFileName());
        }
    }

    public void save() {
        try {
            if (document != null) document.save();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + type.getFileName());
        }
    }
}