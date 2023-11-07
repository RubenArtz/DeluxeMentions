package ruben_artz.main.spigot;

import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.resolver.data.Repository;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import ruben_artz.main.spigot.launcher.MSLaunch;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;
import ruben_artz.main.spigot.config.Configurations;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public final class DeluxeMentions extends JavaPlugin {
    public PluginDescriptionFile file = getDescription();
    public String table = file.getName().toLowerCase()+"_1_0";
    @Getter public String version = file.getVersion();
    public String latestversion;
    @Getter public String prefix = "&8[&9Deluxe Mentions&8]&f ";
    public List<String> authors = file.getAuthors();
    public String web = file.getWebsite();
    public Configurations fileUtilsSpigot;
    private MSLaunch launch;
    @Getter public Set<UUID> IgnoreMention = new HashSet<>();

    @Override
    public void onLoad() {
        getLogger().info("Verifying the dependencies...");

        try {
            Path downloadPath = Paths.get(getDataFolder().getPath() + File.separator + "cache");
            ApplicationBuilder.appending("DeluxeMentions")
                    .downloadDirectoryPath(downloadPath)
                    .mirrorSelector((a, b) -> a)
                    .internalRepositories(Collections.singleton(new Repository(new URL("https://repo1.maven.org/maven2/"))))
                    .build();

            getLogger().info("Dependencies successfully loaded!");
        } catch (ReflectiveOperationException | IOException | URISyntaxException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public void onEnable() {
        try {
            this.launch = Class.forName("ruben_artz.main.spigot.launcher.MSLauncher").asSubclass(MSLaunch.class).newInstance();
            ProjectUtil.syncRunTask(() -> DeluxeMentions.this.launch.launch(DeluxeMentions.this));
        } catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
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
                .setLanguageFile("lang/"+getConfig().getString("MENTION.LANGUAGE")+".yml");
    }
    public Audience getAudiences(Player player) {
        return MSLauncher.getInstance().audiences.player(player);
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

    public void LoadAllConfigs(){
        saveDefaultConfig();
        reloadConfig();
        initiate();
    }
    public void sendConsole(String msg){
        Bukkit.getConsoleSender().sendMessage(addColor.addColors(msg));
    }
    public String getLatestVersion()
    {
        return this.latestversion;
    }

    public void Messages(){
        sendConsole(prefix + "&aSuccessfully enabled &cv" + version);
        sendConsole("&8--------------------------------------------------------------------------------------");
        sendConsole("&7         Developed by &cRuben_Artz");
        sendConsole(prefix + "§aVersion: §c" + version+" &ais loading...");
        sendConsole(prefix + "&aServer: &c"+Bukkit.getVersion());
        sendConsole(prefix + "&aLoading necessary files...");
        sendConsole("&f");
        sendConsole("&fDeluxe Mentions Starting plugin...");
        sendConsole("&f");
        sendConsole(prefix + "§aSuccessfully loaded files");
        sendConsole("&f");
        sendConsole("&8--------------------------------------------------------------------------------------");
    }
}
