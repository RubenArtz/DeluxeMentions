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

package artzstudio.dev.mentions.spigot.util.slim;

import artzstudio.dev.mentions.spigot.DeluxeMentions;
import io.github.slimjar.app.builder.ApplicationBuilder;
import io.github.slimjar.app.builder.SpigotApplicationBuilder;
import io.github.slimjar.injector.loader.factory.InjectableFactory;
import io.github.slimjar.logging.ProcessLogger;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class SlimJar {
    private static final boolean DEBUG = Boolean.getBoolean("deluxe-mentions.debug-slimjar");
    private static final boolean DISABLE_REMAPPER = Boolean.getBoolean("deluxe-mentions.disable-remapper");

    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean loaded = new AtomicBoolean();

    public static void load(DeluxeMentions plugin) {
        if (loaded.get()) return;
        lock.lock();

        try {
            if (loaded.getAndSet(true)) return;

            final var downloadPath = plugin.getDataFolder().getParentFile().toPath()
                    .resolve("Artz-Libraries")
                    .resolve(plugin.getName());

            ProcessLogger customLogger = new ProcessLogger() {
                @Override
                public void info(@NonNull String message, @Nullable Object... args) {
                    plugin.getLogger().info(message.formatted(args));
                }

                @Override
                public void error(@NonNull String message, @Nullable Object... args) {
                    plugin.getLogger().severe(message.formatted(args));
                }

                @Override
                public void debug(@NonNull String message, @Nullable Object... args) {
                    if (DEBUG) plugin.getLogger().info("[DEBUG] " + message.formatted(args));
                }
            };

            plugin.getLogger().info("Loading libraries...");

            try {
                new SpigotApplicationBuilder(plugin)
                        .logger(customLogger)
                        .downloadDirectoryPath(downloadPath)
                        .debug(DEBUG)
                        .remap(!DISABLE_REMAPPER)
                        .build();
            } catch (Throwable e) {
                try {
                    ApplicationBuilder.appending(plugin.getName())
                            .logger(customLogger)
                            .injectableFactory(InjectableFactory.selecting(InjectableFactory.ERROR, InjectableFactory.INJECTABLE, InjectableFactory.WRAPPED, InjectableFactory.UNSAFE))
                            .downloadDirectoryPath(downloadPath)
                            .build();
                } catch (Throwable fallbackError) {
                    plugin.getLogger().severe("CRITICAL: Failed to download/load libraries via fallback!");

                    fallbackError.printStackTrace();
                }

                e.printStackTrace();
            }
            plugin.getLogger().info("Libraries loaded successfully!");
        } finally {
            lock.unlock();
        }
    }
}