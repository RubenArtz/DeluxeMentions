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

package artzstudio.dev.mentions.spigot.launcher;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import artzstudio.dev.mentions.spigot.commands.main.RegisterCommand;
import artzstudio.dev.mentions.spigot.database.Cache;
import artzstudio.dev.mentions.spigot.events.mention.everyone;
import artzstudio.dev.mentions.spigot.events.mention.target;
import artzstudio.dev.mentions.spigot.events.playerJoin;
import artzstudio.dev.mentions.spigot.events.playerLeave;
import artzstudio.dev.mentions.spigot.events.updateEvent;
import artzstudio.dev.mentions.spigot.inventory.Inventory;
import artzstudio.dev.mentions.spigot.placeholder.Placeholder;
import artzstudio.dev.mentions.spigot.util.NotificationManager;
import artzstudio.dev.mentions.spigot.util.UtilityFunctions;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Launcher implements Launch {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    @Getter
    private static Launcher instance;
    @Getter
    private static TaskScheduler scheduler;
    public BukkitAudiences audiences;
    @Getter
    private Cache cache;

    @Override
    public void launch(DeluxeMentions plugin) {
        instance = this;

        audiences = BukkitAudiences.create(plugin);

        scheduler = UniversalScheduler.getScheduler(plugin);

        this.loadAllConfigs();
        this.setCommands();
        this.registerPlaceholders();
        this.setEvents();
        this.setMetrics();
        this.setConnection();
        this.checkBypass();
        this.checkBypassAdmin();
        this.welcome();
    }

    @Override
    public void shutdown() {
        if (getCache() != null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> getCache().getMethod().shutdown());

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    plugin.getLogger().warning("Cache took too long to shut down. Skipping it.");
                }
            } catch (InterruptedException ignored) {
            }
        }

        plugin.clearIgnoredPlayers();

        NotificationManager.shutdown();
    }

    public void loadAllConfigs() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        plugin.initiate();
    }

    private void setMetrics() {
        final Metrics metrics = new Metrics(plugin, 5770);
        metrics.addCustomChart(new SingleLineChart("players", () -> Bukkit.getOnlinePlayers().size()));
    }

    private void setConnection() {
        cache = new Cache();
    }

    public void setCommands() {
        Objects.requireNonNull(plugin.getCommand("deluxementions")).setExecutor(new RegisterCommand());
        Objects.requireNonNull(plugin.getCommand("mention")).setExecutor(new artzstudio.dev.mentions.spigot.commands.other.RegisterCommand());
    }

    private void setEvents() {
        PluginManager event = plugin.getServer().getPluginManager();
        Arrays.asList(
                new updateEvent(),
                new Inventory(),
                new playerJoin(),
                new playerLeave(),
                new target(),
                new everyone()).forEach(listener -> event.registerEvents(listener, plugin));

        NotificationManager.launch();
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
    }

    private void checkBypass() {
        UtilityFunctions.delayMention = new HashMap<>();

        UtilityFunctions.runTaskTimer(20, () -> {
            HashMap<UUID, Integer> tempPlayers = new HashMap<>();
            for (UUID uuid : UtilityFunctions.getDelayMention().keySet()) {
                Integer newVal = UtilityFunctions.getDelayMention().get(uuid);
                newVal--;

                if (newVal > 0) {
                    tempPlayers.put(uuid, newVal);
                }
            }
            UtilityFunctions.delayMention = tempPlayers;
        });
    }

    private void checkBypassAdmin() {
        UtilityFunctions.delayMentionAdmin = new HashMap<>();

        UtilityFunctions.runTaskTimer(20, () -> {
            HashMap<UUID, Integer> tempPlayers = new HashMap<>();
            for (UUID uuid : UtilityFunctions.getDelayMentionAdmin().keySet()) {
                Integer newVal = UtilityFunctions.getDelayMentionAdmin().get(uuid);
                newVal--;

                if (newVal > 0) {
                    tempPlayers.put(uuid, newVal);
                }
            }
            UtilityFunctions.delayMentionAdmin = tempPlayers;
        });
    }

    public void welcome() {
        String linea1 = "___  ____ _    _  _ _  _ ____    _  _ ____ _  _ ___ _ ____ _  _ ____ ";
        String linea2 = "|  \\ |___ |    |  |  \\/  |___    |\\/| |___ |\\ |  |  | |  | |\\ | [__ ";
        String linea3 = "|__/ |___ |___ |__| _/\\_ |___    |  | |___ | \\|  |  | |__| | \\| ___]";

        String color1 = "#54daf4";
        String color2 = "#545eb6";

        plugin.sendConsole("<gradient:" + color1 + ":" + color2 + ">" + linea1 + "</gradient><reset>");
        plugin.sendConsole("<gradient:" + color1 + ":" + color2 + ">" + linea2 + "</gradient><reset>");
        plugin.sendConsole("<gradient:" + color1 + ":" + color2 + ">" + linea3 + "</gradient><reset>");

        plugin.sendConsole("&f");
        plugin.sendConsole(plugin.prefix + "&aSuccessfully enabled &cv" + plugin.version);
        plugin.sendConsole("&8--------------------------------------------------------------------------------------");
        plugin.sendConsole("&7         Developed by &cRuben_Artz");
        plugin.sendConsole(plugin.prefix + "&aVersion: &c" + plugin.version + " &ais loading...");
        plugin.sendConsole(plugin.prefix + "&aServer: &c" + Bukkit.getVersion());
        plugin.sendConsole(plugin.prefix + "&aLoading necessary files...");
        plugin.sendConsole("&f");
        plugin.sendConsole("&fDeluxe Mentions Starting plugin...");
        plugin.sendConsole("&f");
        plugin.sendConsole(plugin.prefix + "&aSuccessfully loaded files");
        plugin.sendConsole("&f");
        plugin.sendConsole("&8--------------------------------------------------------------------------------------");
    }
}