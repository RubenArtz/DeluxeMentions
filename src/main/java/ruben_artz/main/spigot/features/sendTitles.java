package ruben_artz.main.spigot.features;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import ruben_artz.main.spigot.DeluxeMentions;
import ruben_artz.main.spigot.other.ProjectUtil;
import ruben_artz.main.spigot.other.addColor;

import java.time.Duration;
import java.util.Objects;

public class sendTitles {
    private static final DeluxeMentions plugin = DeluxeMentions.getPlugin(DeluxeMentions.class);

    public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subTitle) {
        Audience audience = plugin.getAudiences(player);

        title = Objects.requireNonNull(title)
                .replace("{Player}", player.getName())
                .replace("{Address}", String.valueOf(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
                .replace("{Uuid}", player.getUniqueId().toString());
        subTitle = Objects.requireNonNull(subTitle)
                .replace("{Player}", player.getName())
                .replace("{Address}", String.valueOf(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress()))
                .replace("{Uuid}", player.getUniqueId().toString());
        Component text_title = addColor.setColor(player, title);
        Component text_subTitle = addColor.setColor(player, subTitle);

        ProjectUtil.syncRunTask(() -> {
            Title createTitle = Title.title(text_title, text_subTitle, Title.Times.times(Duration.ofSeconds(fadeIn), Duration.ofSeconds(stay), Duration.ofMillis(fadeOut)));

            audience.showTitle(createTitle);
        });
    }
}