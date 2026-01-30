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

package artzstudio.dev.mentions.spigot;

import artzstudio.dev.mentions.spigot.config.ConfigType;
import artzstudio.dev.mentions.spigot.config.ConfigurationManager;
import artzstudio.dev.mentions.spigot.launcher.Launch;
import artzstudio.dev.mentions.spigot.launcher.Launcher;
import artzstudio.dev.mentions.spigot.util.addColor;
import artzstudio.dev.mentions.spigot.util.slim.SlimJar;
import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DeluxeMentions extends JavaPlugin {
    @Getter
    private final Set<UUID> ignoredPlayers = ConcurrentHashMap.newKeySet();
    public PluginDescriptionFile file = getDescription();
    public String table = file.getName().toLowerCase();
    @Getter
    public String version = file.getVersion();
    @Getter
    public String prefix = "&8[&9Deluxe Mentions&8]&f ";
    @Getter
    private ConfigurationManager configManager;
    private Launch launch;

    @Override
    public void onLoad() {
        SlimJar.load(this);
    }

    public void onEnable() {
        try {
            this.launch = Class.forName("artzstudio.dev.mentions.spigot.launcher.Launcher")
                    .asSubclass(Launch.class)
                    .getDeclaredConstructor()
                    .newInstance();

            DeluxeMentions.this.launch.launch(DeluxeMentions.this);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDisable() {
        if (this.launch != null) {
            this.launch.shutdown();
            this.launch = null;
        }
    }

    public void initiate() {
        this.configManager = new ConfigurationManager(this);

        this.configManager.loadAll();
    }

    public YamlDocument getFileTranslations() {
        String selectedLang = getConfigYaml().getString("MENTION.LANGUAGE", "en_US");

        for (ConfigType type : ConfigType.values()) {
            if ("lang".equals(type.getSubFolder()) && type.getFileName().startsWith(selectedLang)) {
                return configManager.get(type);
            }
        }

        return configManager.get(ConfigType.LANG_EN);
    }

    public YamlDocument getConfigYaml() {
        return configManager.get(ConfigType.CONFIG);
    }

    public YamlDocument getGroups() {
        return configManager.get(ConfigType.GROUPS);
    }

    public boolean isIgnoring(UUID uuid) {
        return ignoredPlayers.contains(uuid);
    }

    public void setIgnoring(UUID uuid, boolean ignore) {
        if (ignore) {
            ignoredPlayers.add(uuid);
        } else {
            ignoredPlayers.remove(uuid);
        }
    }

    public void clearIgnoredPlayers() {
        ignoredPlayers.clear();
    }

    public Audience getAudiences(Player player) {
        if (Launcher.getInstance().audiences == null) {

            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }

        return Launcher.getInstance().audiences.player(player);
    }

    public Audience getAudiences() {
        if (Launcher.getInstance().audiences == null) {

            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }

        return Launcher.getInstance().audiences.console();
    }

    public void sendConsole(String msg) {
        Audience audience = getAudiences();

        audience.sendMessage(addColor.setColor(msg));
    }
}