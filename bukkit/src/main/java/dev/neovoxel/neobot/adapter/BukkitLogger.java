package dev.neovoxel.neobot.adapter;

import dev.neovoxel.neobot.util.NeoLogger;

import java.util.logging.Logger;

public class BukkitLogger implements NeoLogger {
    private final Logger logger;

    public BukkitLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.severe(message);
        throwable.printStackTrace();
    }

    @Override
    public void debug(String message) {
        logger.fine(message);
    }

    @Override
    public void trace(String message) {
        logger.finest(message);
    }
}
