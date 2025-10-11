package ruben_artz.mention.config;

import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.util.UtilityFunctions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class UtilUpdateConfig {
    private final static DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void updateConfigs() {
        UtilityFunctions.runTaskAsynchronously(() -> {
            /*
            Update config of "config.yml"
             */
            if (!Objects.equals(plugin.getConfig().getString("version"), "1.7")) {
                try {
                    Files.copy(Paths.get(plugin.getDataFolder() + "/config.yml"), Paths.get(plugin.getDataFolder() + "/old-config-" + plugin.getConfig().getString("version") + ".yml"), StandardCopyOption.REPLACE_EXISTING);
                    File file = new File(plugin.getDataFolder(), "config.yml");
                    file.delete();
                    plugin.saveDefaultConfig();
                    plugin.sendConsole(plugin.prefix + "&cYour config.yml folder was updated in this version!");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            /*
            Update config of "groups.yml"
             */
            if (!Objects.equals(plugin.getGroups().getString("version"), "1.3")) {
                try {
                    Files.copy(Paths.get(plugin.getDataFolder() + "/groups.yml"), Paths.get(plugin.getDataFolder() + "/old-groups-" + plugin.getGroups().getString("version") + ".yml"), StandardCopyOption.REPLACE_EXISTING);
                    File file = new File(plugin.getDataFolder(), "groups.yml");
                    file.delete();
                    plugin.initiate();
                    plugin.sendConsole(plugin.prefix + "&cYour groups.yml folder was updated in this version!");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            /*
            Update config of "lang.file"
             */
            UtilityFunctions.runTaskLater(10L, () -> {
                if (!Objects.requireNonNull(plugin.getLangVersion().getString("version")).contains("1.5")) {
                    File f = new File(plugin.getDataFolder(), "/lang/");
                    File[] files = f.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                    plugin.initiate();
                    plugin.sendConsole(plugin.prefix + "&cYour lang folder was updated in this version!");
                }
            });
        });
    }
}
