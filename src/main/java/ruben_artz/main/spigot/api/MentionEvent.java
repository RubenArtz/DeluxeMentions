package ruben_artz.main.spigot.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MentionEvent extends Event implements Cancellable {
    @Getter
    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final Player sender;
    @Getter
    @Setter
    private boolean cancelled;

    public MentionEvent(Player who, Player sender) {
        this.player = who;
        this.sender = sender;
    }

    @SuppressWarnings("NullableProblems")
    public HandlerList getHandlers() {
        return handlerList;
    }
}
