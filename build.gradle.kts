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

plugins {
    id("java")
    alias(libs.plugins.shadow)
    alias(libs.plugins.slimjar)
}

version = "7.2.21"

val slimJarBase = "artzstudio.dev.mentions.spigot.slimjar."
val libsBase = "artzstudio.dev.mentions.spigot.relocated."

registerOutputTask("Ruben_Artz", "F:/Ruben_Artz/Artz Studio/1.21.11/plugins")

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://libraries.minecraft.net/") }
    maven { url = uri("https://repository.rubenmatias.com/releases") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    compileOnly(libs.spigotmc)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    compileOnly(libs.placeholderapi)
    compileOnly(libs.authlib)

    compileOnly(fileTree(mapOf("dir" to "libs", "includes" to listOf("*.jar"))))

    implementation(libs.slimjarRuntime)
    implementation(libs.slimjarHelperSpigot)
    implementation(libs.minimessage)
    implementation(libs.adventurePlatformBukkit)

    slim(libs.xseries)
    slim(libs.hikarycp)
    slim(libs.h2)
    slim(libs.universalScheduler)
    slim(libs.okhttps)
    slim(libs.bstats.bukkit)
    slim(libs.boostedYaml)
    slim(libs.gson)
}



tasks {
    shadowJar {
        archiveFileName.set("Deluxe Mentions.jar")

        relocate("io.github.slimjar", "${libsBase}slimjar")
        relocate("net.kyori", "${libsBase}kyori")
    }

    slimJar {
        relocate("com.cryptomorin.xseries", "${slimJarBase}xseries")
        relocate("com.zaxxer.hikari", "${slimJarBase}hikari")
        relocate("org.h2", "${slimJarBase}h2")
        relocate("com.github.Anon8281.universalScheduler", "${slimJarBase}universalScheduler")
        relocate("okhttp3", "${slimJarBase}okhttp3")
        relocate("dev.dejvokep.boostedyaml", "${slimJarBase}boostedyaml")
        relocate("org.bstats", "${libsBase}libs.bstats")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

fun registerOutputTask(name: String, path: String) {
    if (!System.getProperty("os.name").lowercase().contains("windows")) {
        return
    }

    tasks.register<Copy>("build$name") {
        group = "build plugin"
        dependsOn(tasks.shadowJar)
        from(tasks.shadowJar.get().archiveFile)
        into(file(path))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}