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

package artzstudio.dev.mentions.spigot.config;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.EnumMap;
import java.util.Map;

public class ConfigurationManager {

    private final DeluxeMentions plugin;
    private final Map<ConfigType, YamlConfig> configs;

    public ConfigurationManager(DeluxeMentions plugin) {
        this.plugin = plugin;
        this.configs = new EnumMap<>(ConfigType.class);
    }

    public void loadAll() {
        for (ConfigType type : ConfigType.values()) {
            configs.put(type, new YamlConfig(plugin, type));
        }
    }

    public YamlDocument get(ConfigType type) {
        if (!configs.containsKey(type)) {
            configs.put(type, new YamlConfig(plugin, type));
        }
        return configs.get(type).getDocument();
    }

    public void reloadAll() {
        configs.values().forEach(YamlConfig::reload);
    }

    public void saveAll() {
        configs.values().forEach(YamlConfig::save);
    }
}