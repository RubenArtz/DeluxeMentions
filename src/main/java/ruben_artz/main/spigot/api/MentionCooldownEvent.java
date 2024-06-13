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

    @Getter @Setter
    private boolean cancelled;

    @Getter
    private final Player player;

    @Getter
    private final Player mentioned;

    public MentionCooldownEvent(Player who, Player mentioned) {
        this.player = who;
        this.mentioned = mentioned;
    }

    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}