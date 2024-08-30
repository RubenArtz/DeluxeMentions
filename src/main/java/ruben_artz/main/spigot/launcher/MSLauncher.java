package ruben_artz.main.spigot.launcher;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.commands.main.RegisterCommand;
import ruben_artz.main.spigot.database.Cache;
import ruben_artz.main.spigot.events.mention.everyone;
import ruben_artz.main.spigot.events.mention.target;
import ruben_artz.main.spigot.events.playerJoin;
import ruben_artz.main.spigot.events.playerLeave;
import ruben_artz.main.spigot.inventory.MSInventory;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.placeholder.MSPlaceholder;
import ruben_artz.main.spigot.util.MSUpdater;
import ruben_artz.main.spigot.util.UtilPlayer;
import ruben_artz.main.spigot.config.UtilUpdateConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MSLauncher implements MSLaunch {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @Getter private static MSLauncher instance;
    @Getter private Cache cache;

    @Getter private static TaskScheduler scheduler;

    public BukkitAudiences audiences;

    @Override
    public void launch(DeluxeMentions plugin) {
        instance = this;

        audiences = BukkitAudiences.create(plugin);

        scheduler = UniversalScheduler.getScheduler(plugin);

        plugin.LoadAllConfigs();
        UtilUpdateConfig.updateConfigs();
        setCommands();
        registerPlaceholders();
        setEvents();
        updateChecker(UPDATER.CONSOLE);
        plugin.Messages();
        setMetrics();
        setConnection();
        checkBypass();
        checkBypassAdmin();
    }

    @Override
    public void shutdown() {
        if(getCache() != null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> getCache().getMethod().shutdown());

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    plugin.getLogger().warning("Cache took too long to shut down. Skipping it.");
                }
            }catch(InterruptedException ignored) {}
        }
    }

    private void setMetrics() {
        final Metrics metrics = new Metrics(plugin,5770);
        metrics.addCustomChart(new SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
    }

    private void setConnection() {
        cache = new Cache();
    }

    public void setCommands() {
        Objects.requireNonNull(plugin.getCommand("deluxementions")).setExecutor(new RegisterCommand());
        Objects.requireNonNull(plugin.getCommand("mention")).setExecutor(new ruben_artz.main.spigot.commands.other.RegisterCommand());
    }
    private void setEvents(){
        PluginManager event = plugin.getServer().getPluginManager();
        Arrays.asList(
                new MSUpdater(),
                new UtilPlayer(),
                new MSInventory(),
                new playerJoin(),
                new playerLeave(),
                new target(),
                new everyone()).forEach(listener -> event.registerEvents(listener, plugin));
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MSPlaceholder().register();
        }
    }

    public enum UPDATER {
        JOIN_PLAYER, CONSOLE
    }

    private void checkBypass() {
        ProjectUtil.delayMention = new HashMap<>();

        ProjectUtil.runTaskTimer(20, () -> {
            HashMap<UUID, Integer> tempPlayers = new HashMap<>();
            for (UUID uuid : ProjectUtil.getDelayMention().keySet()) {
                Integer newVal = ProjectUtil.getDelayMention().get(uuid);
                newVal--;

                if (newVal > 0) {
                    tempPlayers.put(uuid, newVal);
                }
            }
            ProjectUtil.delayMention = tempPlayers;
        });
    }

    private void checkBypassAdmin() {
        ProjectUtil.delayMentionAdmin = new HashMap<>();

        ProjectUtil.runTaskTimer(20, () -> {
            HashMap<UUID, Integer> tempPlayers = new HashMap<>();
            for (UUID uuid : ProjectUtil.getDelayMentionAdmin().keySet()) {
                Integer newVal = ProjectUtil.getDelayMentionAdmin().get(uuid);
                newVal--;

                if (newVal > 0) {
                    tempPlayers.put(uuid, newVal);
                }
            }
            ProjectUtil.delayMentionAdmin = tempPlayers;
        });
    }

    public void updateChecker(UPDATER type) {
        switch (type) {
            case CONSOLE: {
                ProjectUtil.runTask(() -> ProjectUtil.runTaskTimer("HOURS", 5, () -> {
                    try {
                        HttpURLConnection con = (HttpURLConnection)new URL("https://api.spigotmc.org/legacy/update.php?resource=67248").openConnection();
                        int timed_out = 1250;
                        con.setConnectTimeout(timed_out);
                        con.setReadTimeout(timed_out);
                        plugin.latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                        if ((plugin.latestversion.length() <= 7) && (!plugin.version.equals(plugin.latestversion))) {
                            plugin.sendConsole( "&8--------------------------------------------------------------------------------------");
                            plugin.sendConsole( plugin.prefix+"&fYou have an old version of the &eDeluxe Mentions &fplugin.");
                            plugin.sendConsole( plugin.prefix+"&fPlease download the latest &e"+ plugin.getLatestVersion()+" &fversion.");
                            plugin.sendConsole( "&8--------------------------------------------------------------------------------------");
                        }
                    }
                    catch (Exception ignore) {}
                }));
                break;
            }
            case JOIN_PLAYER: {
                try {
                    HttpURLConnection con = (HttpURLConnection)new URL("https://api.spigotmc.org/legacy/update.php?resource=67248").openConnection();
                    int timed_out = 1250;
                    con.setConnectTimeout(timed_out);
                    con.setReadTimeout(timed_out);
                    plugin.latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
                    if ((plugin.latestversion.length() <= 7) && (!plugin.version.equals(plugin.latestversion))) {
                        plugin.sendConsole( "&8--------------------------------------------------------------------------------------");
                        plugin.sendConsole( plugin.prefix+"&fYou have an old version of the &eDeluxe Mentions &fplugin.");
                        plugin.sendConsole( plugin.prefix+"&fPlease download the latest &e"+ plugin.getLatestVersion()+" &fversion.");
                        plugin.sendConsole( "&8--------------------------------------------------------------------------------------");
                    }
                }
                catch (Exception ignore) {}
                break;
            }
        }

    }
}
