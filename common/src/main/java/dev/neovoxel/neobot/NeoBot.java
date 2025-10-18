package dev.neovoxel.neobot;

import dev.neovoxel.neobot.bot.BotProvider;
import dev.neovoxel.neobot.command.CommandProvider;
import dev.neovoxel.neobot.config.ConfigProvider;
import dev.neovoxel.neobot.game.GameEventListener;
import dev.neovoxel.neobot.game.GameProvider;
import dev.neovoxel.neobot.library.LibraryProvider;
import dev.neovoxel.neobot.scheduler.SchedulerProvider;
import dev.neovoxel.neobot.script.ScriptProvider;
import dev.neovoxel.neobot.storage.StorageProvider;
import dev.neovoxel.neobot.util.CommandSender;
import dev.neovoxel.neobot.util.NeoLogger;
import org.slf4j.Logger;

import java.io.File;
import java.net.URISyntaxException;

public interface NeoBot extends ConfigProvider, GameProvider, LibraryProvider, SchedulerProvider, StorageProvider {
    default void enable() {
        try {
            getNeoLogger().info("Loading libraries...");
            loadBasicLibrary(this);
            getNeoLogger().info("Loading config...");
            loadConfig(this);
            getNeoLogger().info("Loading storage...");
            loadStorage(this);
            getNeoLogger().info("Loading game events...");
            setGameEventListener(new GameEventListener(this));
            getNeoLogger().info("Registering commands...");
            registerCommands();
            getNeoLogger().info("Loading bot...");
            BotProvider botProvider = new BotProvider(this);
            setBotProvider(botProvider);
            getBotProvider().loadBot(this);
            getNeoLogger().info("Loading script system...");
            submitAsync(() -> {
                try {
                    ScriptProvider scriptProvider = new ScriptProvider(this);
                    setScriptProvider(scriptProvider);
                    getScriptProvider().loadScript(this);
                } catch (Throwable e) {
                    getNeoLogger().error("Failed to load script system", e);
                }
            });
        } catch (Throwable e) {
            getNeoLogger().error("Failed to load the plugin", e);
        }
    }
    
    default void disable() {
        getNeoLogger().info("Unloading all scripts...");
        getScriptProvider().unloadScript();
        getBotProvider().getBotListener().reset();
        getNeoLogger().info("Cancelling all the tasks...");
        cancelAllTasks();
        getNeoLogger().info("Disconnecting to the bot...");
        getBotProvider().unloadBot();
        getNeoLogger().info("Saving data...");
        closeStorage();
    }

    default void reload(CommandSender sender) {
        getScriptProvider().setScriptSystemLoaded(false);
        getNeoLogger().info("Reloading config...");
        reloadConfig(this);
        submitAsync(() -> {
            getNeoLogger().info("Reloading bot...");
            getBotProvider().reloadBot();
            getBotProvider().getBotListener().reset();
            getNeoLogger().info("Reloading scripts...");
            try {
                getScriptProvider().unloadScript();
                getScriptProvider().loadScript(this);
            } catch (Throwable e) {
                getNeoLogger().error("Failed to reload scripts", e);
            }
            if (sender != null) {
                sender.sendMessage(getMessageConfig().getMessage("internal.reload.reloaded"));
            }
        });
    }

    NeoLogger getNeoLogger();

    File getDataFolder();

    void setGameEventListener(GameEventListener listener);

    GameEventListener getGameEventListener();

    BotProvider getBotProvider();

    void setBotProvider(BotProvider botProvider);

    ScriptProvider getScriptProvider();

    void setScriptProvider(ScriptProvider scriptProvider);

    CommandProvider getCommandProvider();

    void setCommandProvider(CommandProvider commandProvider);

    void registerCommands();

    void cancelAllTasks();
}
