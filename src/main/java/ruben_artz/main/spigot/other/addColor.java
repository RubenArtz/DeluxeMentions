package ruben_artz.main.spigot.other;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class addColor {
    private static final LegacyComponentSerializer unusualHexSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static @NotNull Component setColor(Player player, String input) {
        return ProjectUtil.setPlaceholders(player, MiniMessage.miniMessage().deserialize(color(input)));
    }

    public static String setColors(Player player, String input) {
        return ChatColor.translateAlternateColorCodes('&', ProjectUtil.setPlaceholders(player, input));
    }

    public static String addColors(Player player, String input) {
        if ((input == null) || input.isEmpty()) return input;
        return unusualHexSerializer.serialize(ProjectUtil.setPlaceholders(player, MiniMessage.miniMessage().deserialize(color(input))));
    }

    public static String addColors(String input) {
        if ((input == null) || (input.isEmpty())) return input;
        final Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(input);
        return unusualHexSerializer.serialize(component);
    }

    public static List<String> addColors(List<String> input) {
        if ((input == null) || (input.isEmpty())) return input;
        return input.stream().map(addColor::addColors).collect(Collectors.toList());
    }

    private static final ImmutableMap<String, String> colorReplacements = new ImmutableMap.Builder<String, String>()
            .put("0", "<black>")
            .put("1", "<dark_blue>")
            .put("2", "<dark_green>")
            .put("3", "<dark_aqua>")
            .put("4", "<dark_red>")
            .put("5", "<dark_purple>")
            .put("6", "<gold>")
            .put("7", "<gray>")
            .put("8", "<dark_gray>")
            .put("9", "<blue>")
            .put("a", "<green>")
            .put("b", "<aqua>")
            .put("c", "<red>")
            .put("d", "<light_purple>")
            .put("e", "<yellow>")
            .put("f", "<white>")
            .put("k", "<magic>")
            .put("l", "<bold>")
            .put("m", "<strikethrough>")
            .put("n", "<underlined>")
            .put("o", "<italic>")
            .put("r", "<reset>")
            .build();

    private static String color(String msg) {
        for(Map.Entry<String, String> entry : colorReplacements.entrySet()) {
            String legacy = entry.getKey();
            String mini = entry.getValue();
            msg = msg.replaceAll(Matcher.quoteReplacement("&"+legacy), Matcher.quoteReplacement(mini));
            msg = msg.replaceAll(Matcher.quoteReplacement(((char)0x00b7)+legacy), Matcher.quoteReplacement(mini));
        }
        return msg;
    }
}
