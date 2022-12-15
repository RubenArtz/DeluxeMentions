package ruben_artz.main.spigot.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MentionEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private final Player player;
    private final Player sender;

    public MentionEvent(Player who, Player sender) {
        this.player = who;
        this.sender = sender;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getSender() {
        return sender;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @SuppressWarnings("NullableProblems")
    public HandlerList getHandlers() {
        return handlerList;
    }
}
