package ruben_artz.main.spigot.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MentionMyselfEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private final Player player;

    public MentionMyselfEvent(Player who) {
        this.player = who;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
