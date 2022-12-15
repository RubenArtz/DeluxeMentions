package ruben_artz.main.spigot.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MentionCooldownEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private final Player player;
    private final Player mentioned;

    public MentionCooldownEvent(Player who, Player mentioned) {
        this.player = who;
        this.mentioned = mentioned;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getMentioned() {
        return mentioned;
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

    public HandlerList getHandlers() {
        return handlerList;
    }
}
