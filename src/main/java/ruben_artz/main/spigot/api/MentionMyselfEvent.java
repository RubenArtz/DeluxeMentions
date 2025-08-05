package ruben_artz.main.spigot.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MentionMyselfEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final Player player;
    private boolean cancelled;

    public MentionMyselfEvent(Player who) {
        this.player = who;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
