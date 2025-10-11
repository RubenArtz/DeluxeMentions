package ruben_artz.mention.util;

import io.github.slimjar.logging.ProcessLogger;
import ruben_artz.mention.DeluxeMentions;

import java.text.MessageFormat;

public class SlimJarLogger implements ProcessLogger {
    private final DeluxeMentions plugin;

    public SlimJarLogger(DeluxeMentions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(String message, Object... args) {

        plugin.getLogger().info(MessageFormat.format(message, args));
    }

    @Override
    public void debug(String message, Object... args) {
        ProcessLogger.super.debug(message, args);
    }
}