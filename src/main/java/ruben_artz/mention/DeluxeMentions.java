package ruben_artz.mention;

import io.github.slimjar.app.builder.ApplicationBuilder;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import ruben_artz.mention.config.Configurations;
import ruben_artz.mention.launcher.Launch;
import ruben_artz.mention.launcher.Launcher;
import ruben_artz.mention.util.SlimJarLogger;
import ruben_artz.mention.util.addColor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class DeluxeMentions extends JavaPlugin {
    public PluginDescriptionFile file = getDescription();
    public String table = file.getName().toLowerCase() + "_1_0";
    public Configurations fileUtilsSpigot;
    @Getter
    public String version = file.getVersion();
    @Getter
    public String prefix = "&8[&9Deluxe Mentions&8]&f ";
    @Getter
    public Set<UUID> IgnoreMention = new HashSet<>();
    private Launch launch;

    @Override
    public void onLoad() {
        getLogger().info("Verifying the dependencies...");

        try {
            Path downloadPath = Paths.get(Bukkit.getWorldContainer().getPath() + File.separator + "plugins" + File.separator + "STN Studios" + File.separator + "Deluxe Mentions");

            ApplicationBuilder.appending("DeluxeMentions")
                    .logger(new SlimJarLogger(this))
                    .downloadDirectoryPath(downloadPath)
                    .mirrorSelector((a, b) -> a)
                    .build();

            getLogger().info("Dependencies successfully loaded!");
        } catch (ReflectiveOperationException | IOException | URISyntaxException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnable() {
        try {
            this.launch = Class.forName("ruben_artz.mention.launcher.Launcher").asSubclass(Launch.class).newInstance();

            DeluxeMentions.this.launch.launch(DeluxeMentions.this);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
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
        fileUtilsSpigot = new Configurations().initiate(this,
                        "lang/en_US.yml",
                        "lang/es_ES.yml",
                        "lang/zh_CH.yml",
                        "lang/fi_FI.yml",
                        "lang/fr_FR.yml",
                        "lang/ko_KR.yml",
                        "lang/th_TH.yml",
                        "lang/tr_TR.yml",
                        "lang/vi_VN.yml",
                        "lang/it_IT.yml",
                        "lang/version.yml",
                        "lang/pt_BR.yml",
                        "groups.yml")
                .setLanguageFile("lang/" + getConfig().getString("MENTION.LANGUAGE") + ".yml");
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

    public Configurations getFileTranslations() {
        return fileUtilsSpigot;
    }

    public FileConfiguration getGroups() {
        return fileUtilsSpigot.getFile("groups.yml");
    }

    public FileConfiguration getLangVersion() {
        return fileUtilsSpigot.getFile("lang/version.yml");
    }

    public void sendConsole(String msg) {
        Audience audience = getAudiences();

        audience.sendMessage(addColor.setColor(msg));
    }
}