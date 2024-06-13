package ruben_artz.main.spigot.config;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Configurations {
    private JavaPlugin plugin;
    private Map<String, YamlConfiguration> files;
    private Map<String, File> filesData;
    private YamlConfiguration langFile = null;

    public Configurations initiate(JavaPlugin plugin, String... fileNames) {
        this.plugin = plugin;
        this.files = new HashMap<>();
        this.filesData = new HashMap<>();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        for (String fileName : fileNames) {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.isDirectory()) {
                if (!file.exists()) {
                    plugin.saveResource(fileName, false);
                }

                if (fileName.endsWith(".yml")) {
                    files.put(fileName, YamlConfiguration.loadConfiguration(file));
                    filesData.put(fileName, file);
                }
            }
        }
        for (File file : Objects.requireNonNull(new File(plugin.getDataFolder() + File.separator + "lang").listFiles())) {
            if (!file.isDirectory()) {
                files.put("lang/" + file.getName(), YamlConfiguration.loadConfiguration(file));
                filesData.put("lang/" + file.getName(), file);
            }
        }
        return this;
    }

    public void reloadFiles() {
        initiate(plugin);
    }

    public YamlConfiguration getFile(String path) {
        return files.get(path);
    }

    public File getFileData(String path) {
        return filesData.get(path);
    }

    public Configurations setLanguageFile(String path) {
        langFile = files.get(path);
        return this;
    }

    public String getString(String path) {
        return langFile.contains(path) ? langFile.getString(path) : "The specified path (lang/../"+path+") could not be found.";
    }

    public List<String> getStringList(final String path) {
        final List<String> lore = Lists.newArrayList();
        lore.addAll(langFile.getStringList(path));
        return lore;
    }

    public int getInt(final String path) {
        return langFile.getInt(path);
    }

    public void saveFile(String path) {
        try {
            getFile(path).save(getFileData(path));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
