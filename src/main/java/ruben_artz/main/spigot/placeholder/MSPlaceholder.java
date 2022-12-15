package ruben_artz.main.spigot.placeholder;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.database.Module;
import ruben_artz.main.spigot.events.antibot.preventAttacks;
import ruben_artz.main.spigot.other.addColor;

import java.util.HashMap;
import java.util.Map;

public class MSPlaceholder extends PlaceholderExpansion implements Configurable {
    public DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    String bypass_true = getString("bypass.true", "&a✔");
    String bypass_false = getString("bypass.false", "&c✖");

    @Override
    public @NotNull String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }
    @Override
    public @NotNull String getIdentifier(){
        return "mentions";
    }
    @Override
    public @NotNull String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "bypass": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (Module.ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (Module.getBool(player.getUniqueId(), "EXCLUDETIMER")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
            case "status": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (Module.ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (Module.getBool(player.getUniqueId(), "MENTION")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
        }
        return super.onRequest(player, params);
    }

    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("bypass.true", "&aYes ✔");
        defaults.put("bypass.false", "&cNo ✖");
        return defaults;
    }
}
