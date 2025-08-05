package ruben_artz.main.spigot.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MentionCooldownEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final Player mentioned;
    @Getter
    @Setter
    private boolean cancelled;
    @Getter
    private Integer delayInSeconds;
    @Getter
    private String formatDelay;

    public MentionCooldownEvent(Player who, Player mentioned, Integer delayInSeconds, String formatDelay) {
        this.player = who;
        this.mentioned = mentioned;
        this.delayInSeconds = delayInSeconds;
        this.formatDelay = formatDelay;
    }

    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}