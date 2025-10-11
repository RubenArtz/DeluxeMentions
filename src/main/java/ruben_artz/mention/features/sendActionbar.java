package ruben_artz.mention.features;

import com.github.Anon8281.universalScheduler.bukkitScheduler.BukkitScheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import ruben_artz.mention.DeluxeMentions;
import ruben_artz.mention.util.UtilityFunctions;
import ruben_artz.mention.util.addColor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class sendActionbar {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void sendActionBar(Player player, String message) {
        Audience audience = plugin.getAudiences(player);

        message = Objects.requireNonNull(message)
                .replace("{Player}", player.getName())
                .replace("{Address}", String.valueOf(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
                .replace("{Uuid}", player.getUniqueId().toString());
        Component text = addColor.setColor(player, message);

        UtilityFunctions.runTask(() -> audience.sendActionBar(text));
    }

    public static void sendActionBar(Player player, String message, long duration) {
        BukkitScheduler bukkitScheduler = new BukkitScheduler(plugin);

        AtomicLong repeater = new AtomicLong(duration);

        bukkitScheduler.runTaskTimer(() -> {

            sendActionBar(player, message);
            repeater.addAndGet(-40L);
            if (repeater.get() < 0L) bukkitScheduler.cancelTasks();

        }, 0L, 40L);
    }
}