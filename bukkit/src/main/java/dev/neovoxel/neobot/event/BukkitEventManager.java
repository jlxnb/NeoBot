package dev.neovoxel.neobot.event;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.adapter.BukkitPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitEventManager implements Listener {
    private final NeoBot plugin;

    public BukkitEventManager(NeoBot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.getGameEventListener().onJoin(new BukkitJoinEvent(event));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getGameEventListener().onQuit(new BukkitPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        plugin.getGameEventListener().onChat(new BukkitChatEvent(event));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        plugin.getGameEventListener().onDeath(new BukkitPlayer(event.getEntity()), event.getDeathMessage());
    }
}
