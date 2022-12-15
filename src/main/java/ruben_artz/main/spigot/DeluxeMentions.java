package ruben_artz.main.spigot;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class DeluxeMentions extends JavaPlugin {
    public PluginDescriptionFile file = getDescription();
    public String table = file.getName().toLowerCase()+"_1_0";
    public String version = file.getVersion();
    public String latestversion;
    public String prefix = "&8[&9Deluxe Mentions&8]&f ";
    public List<String> authors = file.getAuthors();
    public String web = file.getWebsite();
    public Configurations fileUtilsSpigot;
    private MSLaunch launch;
    public Set<UUID> IgnoreMention = new HashSet<>();

    public void onEnable() {
        try {
            this.launch = Class.forName("ruben_artz.main.spigot.launcher.MSLauncher").asSubclass(MSLaunch.class).newInstance();
            ProjectUtil.syncRunTask(() -> DeluxeMentions.this.launch.launch(DeluxeMentions.this));
        } catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
            e.printStackTrace();
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
    public Set<UUID> getIgnoreMention() {
        return IgnoreMention;
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
    public String getVersion() {
        return this.version;
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
        sendConsole("" + prefix + "&aSuccessfully enabled &cv" + version + "");
        sendConsole("&8--------------------------------------------------------------------------------------");
        sendConsole("&7         Developed by &cRuben_Artz");
        sendConsole("" + prefix + "§aVersion: §c" + version+" &ais loading...");
        sendConsole("" + prefix + "&aServer: &c"+Bukkit.getVersion()+"");
        sendConsole("" + prefix + "&aLoading necessary files...");
        sendConsole("&f");
        sendConsole("&fDeluxe Mentions Starting plugin...");
        sendConsole("&f");
        sendConsole("" + prefix + "§aSuccessfully loaded files");
        sendConsole("&f");
        sendConsole("&8--------------------------------------------------------------------------------------");
    }
}
