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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigType {

    // --- LANG ---
    LANG_EN("en_US.yml", "lang", "version"),
    LANG_ES("es_ES.yml", "lang", "version"),
    LANG_FI("fi_FI.yml", "lang", "version"),
    LANG_FR("fr_FR.yml", "lang", "version"),
    LANG_IT("it_IT.yml", "lang", "version"),
    LANG_KO("ko_KR.yml", "lang", "version"),
    LANG_PT("pt_BR.yml", "lang", "version"),
    LANG_TH("th_TH.yml", "lang", "version"),
    LANG_TR("tr_TR.yml", "lang", "version"),
    LANG_VI("vi_VN.yml", "lang", "version"),
    LANG_ZH("zh_CH.yml", "lang", "version"),

    // --- GLOBAL SETTINGS ---
    CONFIG("config.yml", null, "version"),
    GROUPS("groups.yml", null, "version");

    private final String fileName;
    private final String subFolder;
    private final String versionRoute;
}