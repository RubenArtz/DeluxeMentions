package ruben_artz.mention.events.antibot;

import com.lahuca.botsentry.api.BotSentryAPI;
import org.bukkit.Bukkit;

public class preventAttacks {

    public static boolean isAttacking() {
        if (!Bukkit.getPluginManager().isPluginEnabled("BotSentry")) return false;
        BotSentryAPI api = BotSentryAPI.getAPI();
        return api.getAntiBotMode().isAntiBotMode();
    }
}
