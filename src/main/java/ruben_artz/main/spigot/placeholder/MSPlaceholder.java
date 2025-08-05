package ruben_artz.main.spigot.placeholder;

import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.events.antibot.preventAttacks;
import ruben_artz.main.spigot.launcher.MSLauncher;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.HashMap;
import java.util.Map;

public class MSPlaceholder extends PlaceholderExpansion implements Configurable {
    public DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);
    String bypass_true = getString("bypass.true", "&a✔");
    String bypass_false = getString("bypass.false", "&c✖");

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mentions";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "bypass": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (MSLauncher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (MSLauncher.getInstance().getCache().setBool(player.getUniqueId(), "EXCLUDETIMER")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
            case "status": {
                if (preventAttacks.isAttacking()) return "Loading...";
                if (MSLauncher.getInstance().getCache().ifNotExists(player.getUniqueId())) {
                    return "N/A";
                } else {
                    if (MSLauncher.getInstance().getCache().setBool(player.getUniqueId(), "MENTION")) {
                        return addColor.addColors(bypass_true);
                    } else {
                        return addColor.addColors(bypass_false);
                    }
                }
            }
            case "delayInSeconds": {
                if (preventAttacks.isAttacking()) return "Loading...";

                if (ProjectUtil.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                    return String.valueOf(ProjectUtil.getDelayMentionAdmin().get(player.getUniqueId()));
                } else if (ProjectUtil.getDelayMention().containsKey(player.getUniqueId())) {
                    return String.valueOf(ProjectUtil.getDelayMention().get(player.getUniqueId()));
                } else {
                    return "0";
                }
            }
            case "delayInFormat": {
                if (preventAttacks.isAttacking()) return "Loading...";

                if (ProjectUtil.getDelayMentionAdmin().containsKey(player.getUniqueId())) {
                    return ProjectUtil.convertSecondsToHMS(ProjectUtil.getDelayMentionAdmin().get(player.getUniqueId()));
                } else if (ProjectUtil.getDelayMention().containsKey(player.getUniqueId())) {
                    return ProjectUtil.convertSecondsToHMS(ProjectUtil.getDelayMention().get(player.getUniqueId()));
                } else {
                    return "0";
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