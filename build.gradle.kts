plugins {
    id ("java")
    id ("com.github.johnrengelman.shadow") version "8.1.1"
    id ("io.github.slimjar") version "1.3.0"
}

group = "ruben_artz.main"
version = "6.8.21"

registerOutputTask("Ruben_Artz", "D:/Ruben_Artz/STN Studios/Development/plugins")


repositories {
    mavenCentral()
    maven {
        name = ("spigotmc-repo")
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = ("sonatype")
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name  =("minecraft-repo")
        url = uri("https://libraries.minecraft.net/")
    }
    maven { url = uri("https://repo.alessiodp.com/releases/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    compileOnly ("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly ("org.projectlombok:lombok:1.18.30")
    /*
    Keep up to date
    Url: https://www.spigotmc.org/resources/6245/
     */
    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly ("com.mojang:authlib:1.5.21")
    compileOnly ("org.jetbrains:annotations:23.0.0")

    implementation ("io.github.slimjar:slimjar:1.2.7")
    implementation ("net.kyori:adventure-text-minimessage:4.17.0")
    implementation ("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation ("org.bstats:bstats-bukkit:3.0.0")

    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    compileOnly(fileTree(mapOf("dir" to "libs", "includes" to listOf("*.jar"))))

    /*
    Keep up to date
    Url: https://github.com/CryptoMorin/XSeries/releases
     */
    slim ("com.github.cryptomorin:XSeries:12.0.0")
    slim ("org.slf4j:slf4j-simple:1.7.36")
    slim ("com.zaxxer:HikariCP:4.0.3")
    slim ("com.h2database:h2:2.1.214")
    slim ("com.github.Anon8281:UniversalScheduler:0.1.6")
}

tasks.shadowJar {
    archiveFileName.set("Deluxe Mentions.jar")

    relocate("io.github.slimjar", "ruben_artz.libs.slimjar")
    relocate("net.kyori", "ruben_artz.libs.kyori")
    relocate("org.bstats", "ruben_artz.libs.bstats")
}

tasks.slimJar {
    relocate("com.cryptomorin.xseries", "ruben_artz.libs.xseries")
    relocate("com.zaxxer.hikari", "ruben_artz.libs.hikari")
    relocate("org.h2", "ruben_artz.libs.h2")
    relocate("com.github.Anon8281.universalScheduler", "ruben_artz.libs.universalScheduler")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
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