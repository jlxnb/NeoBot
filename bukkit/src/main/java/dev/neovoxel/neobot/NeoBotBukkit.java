package dev.neovoxel.neobot;

import dev.neovoxel.neobot.adapter.*;
import dev.neovoxel.neobot.adapter.executor.BukkitConsoleSender;
import dev.neovoxel.neobot.adapter.executor.DedicatedExecutor;
import dev.neovoxel.neobot.adapter.executor.MinecraftServerExecutor;
import dev.neovoxel.neobot.adapter.executor.NativeExecutor;
import dev.neovoxel.neobot.bot.BotListener;
import dev.neovoxel.neobot.bot.BotProvider;
import dev.neovoxel.neobot.command.CommandProvider;
import dev.neovoxel.neobot.config.EnhancedConfig;
import dev.neovoxel.neobot.config.ScriptConfig;
import dev.neovoxel.neobot.event.BukkitEventManager;
import dev.neovoxel.neobot.game.GameEventListener;
import dev.neovoxel.neobot.scheduler.ScheduledTask;
import dev.neovoxel.neobot.script.Script;
import dev.neovoxel.neobot.script.ScriptProvider;
import dev.neovoxel.neobot.adapter.NeoLogger;
import dev.neovoxel.neobot.adapter.OfflinePlayer;
import dev.neovoxel.neobot.adapter.Player;
import dev.neovoxel.nsapi.DatabaseStorage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.graalvm.polyglot.HostAccess;

import java.util.ArrayList;
import java.util.List;

public class NeoBotBukkit extends JavaPlugin implements NeoBot {
    private GameEventListener listener;
    private BotListener botListener;
    private EnhancedConfig generalConfig;
    private boolean scriptSystemLoaded = false;
    private List<Script> scripts = new ArrayList<>();
    private DatabaseStorage storage;
    private String storageType;
    private BotProvider botProvider;
    private ScriptProvider scriptProvider;
    private CommandProvider commandProvider;
    private EnhancedConfig messageConfig;
    private ScriptConfig scriptConfig;

    @Override
    public NeoLogger getNeoLogger() {
        return new BukkitLogger(getLogger());
    }

    @Override
    public void setGameEventListener(GameEventListener listener) {
        Bukkit.getPluginManager().registerEvents(new BukkitEventManager(this), this);
        this.listener = listener;
    }

    @Override
    public GameEventListener getGameEventListener() {
        return listener;
    }

    @Override
    public BotProvider getBotProvider() {
        return botProvider;
    }

    @Override
    public void setBotProvider(BotProvider botProvider) {
        this.botProvider = botProvider;
    }

    @Override
    public ScriptProvider getScriptProvider() {
        return scriptProvider;
    }

    @Override
    public void setScriptProvider(ScriptProvider scriptProvider) {
        this.scriptProvider = scriptProvider;
    }

    @Override
    public void setGeneralConfig(EnhancedConfig config) {
        this.generalConfig = config;
    }

    @Override
    public EnhancedConfig getGeneralConfig() {
        return generalConfig;
    }

    @Override
    public void setScriptConfig(ScriptConfig scriptConfig) {
        this.scriptConfig = scriptConfig;
    }

    @Override
    public ScriptConfig getScriptConfig() {
        return scriptConfig;
    }

    @Override
    public void setMessageConfig(EnhancedConfig messageConfig) {
        this.messageConfig = messageConfig;
    }

    @Override
    public EnhancedConfig getMessageConfig() {
        return messageConfig;
    }

    @Override
    public ScheduledTask submit(Runnable task) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTask(this, task));
    }

    @Override
    public ScheduledTask submitAsync(Runnable task) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(this, task));
    }

    @Override
    public ScheduledTask submit(Runnable task, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(this, task, delay * 20));
    }

    @Override
    public ScheduledTask submitAsync(Runnable task, long delay) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, task, delay * 20));
    }

    @Override
    public ScheduledTask submit(Runnable task, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(this, task, delay * 20, period * 20));
    }

    @Override
    public ScheduledTask submitAsync(Runnable task, long delay, long period) {
        return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, task, delay * 20, period * 20));
    }

    @Override
    public void cancelAllTasks() {
        BukkitScheduledTask.cancelAll();
    }

    @Override
    public void setStorage(DatabaseStorage storage) {
        this.storage = storage;
    }

    @Override
    @HostAccess.Export
    public DatabaseStorage getStorage() {
        return storage;
    }

    @Override
    public String getStorageType() {
        return storageType;
    }

    @Override
    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    @Override
    public void onEnable() {
        this.enable();
    }

    @Override
    public void onDisable() {
        this.disable();
    }

    @Override
    public void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public void setCommandProvider(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    @Override
    public CommandProvider getCommandProvider() {
        return commandProvider;
    }

    @Override
    public void registerCommands() {
        BukkitCommandProvider commandProvider1 = new BukkitCommandProvider(this);
        commandProvider1.registerCommand();
    }

    @Override
    public Player getOnlinePlayer(String name) {
        return new BukkitPlayer(Bukkit.getPlayer(name));
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        return new BukkitOfflinePlayer(Bukkit.getOfflinePlayer(name));
    }

    @Override
    public Player[] getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            players.add(new BukkitPlayer(player));
        }
        return players.toArray(new Player[0]);
    }

    @Override
    public String externalParsePlaceholder(String message, OfflinePlayer player) {
        try {
            if (player == null) {
                return PlaceholderAPI.setPlaceholders(null, message);
            } else if (Bukkit.getPlayer(player.getName()) == null) {
                return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player.getName()), message);
            } else {
                return PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player.getName()), message);
            }
        } catch (Exception ignored) {
            return message;
        }
    }

    @Override
    public String getPlatform() {
        return "Bukkit";
    }

    @Override
    public boolean isPluginLoaded(String name) {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }

    @Override
    public RemoteExecutor getExecutorByName(String name) {
        if (name.equalsIgnoreCase("native")) {
            return new NativeExecutor();
        } else if (name.equalsIgnoreCase("dedicated")) {
            return new DedicatedExecutor();
        } else if (name.equalsIgnoreCase("minecraft")) {
            return new MinecraftServerExecutor();
        } else if (name.equalsIgnoreCase("bukkit")) {
            return new BukkitConsoleSender();
        }
        return null;
    }
}
