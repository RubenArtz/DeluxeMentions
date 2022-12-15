package ruben_artz.main.spigot.features;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.util.Objects;

public class sendActionbar {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void sendActionBar(Player player, String message) {
        Audience audience = plugin.getAudiences(player);

        message = Objects.requireNonNull(message)
                .replace("{Player}", player.getName())
                .replace("{Address}", String.valueOf(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
                .replace("{Uuid}", player.getUniqueId().toString());
        Component text = addColor.setColor(player, message);

        ProjectUtil.synTaskAsynchronously(() -> audience.sendActionBar(text));
    }

    public static void sendActionBar(Player player, String message, long duration) {

        new BukkitRunnable() {
            long repeater = duration;

            @Override
            public void run() {
                sendActionBar(player, message);
                repeater -= 40L;
                if (repeater - 40 < -20L) cancel();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }
}