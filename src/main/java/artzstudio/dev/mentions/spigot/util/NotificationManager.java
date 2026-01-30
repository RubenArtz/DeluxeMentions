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

package artzstudio.dev.mentions.spigot.util;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.concurrent.TimeUnit;

public class NotificationManager {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    private static final String URL = "https://api.spigotmc.org/legacy/update.php?resource=67248";

    private static final int TIMEOUT = 1250;

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .callTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
            .build();
    private static MyScheduledTask task;

    public static void launch() {
        if (task != null) {
            task.cancel();
        }

        task = new UniversalRunnable() {
            @Override
            public void run() {
                try {
                    getUpdater();
                } catch (Exception e) {
                    plugin.getLogger().severe("Error while checking for updates: " + e.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 5 * 60 * 60 * 20L);
    }

    public static void shutdown() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private static void getUpdater() {
        String latestVersion = fetchLatestVersion();
        if (latestVersion == null) {
            return;
        }

        String currentVersion = plugin.getDescription().getVersion();

        if (!currentVersion.equals(latestVersion)) {
            notifyUpdateAvailable(latestVersion);
        }
    }

    public static String fetchLatestVersion() {
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                plugin.getLogger().warning("Failed to fetch latest version. HTTP code: " + response.code());
                return null;
            }

            return response.body().string().split("\n")[0].trim();
        } catch (Exception e) {
            plugin.getLogger().severe("Error fetching update: " + e.getMessage());
            return null;
        }
    }

    private static void notifyUpdateAvailable(String latestVersion) {
        plugin.sendConsole(plugin.getPrefix() + "&8--------------------------------------------------------------------------------------");
        plugin.sendConsole(plugin.getPrefix() + "&fYou are currently using an &eoutdated version &fof the &eDeluxe Mentions &fplugin.");
        plugin.sendConsole(plugin.getPrefix() + "&fPlease download the latest version (&e" + latestVersion + "&f)");
        plugin.sendConsole(plugin.getPrefix() + "&8--------------------------------------------------------------------------------------");
    }
}
