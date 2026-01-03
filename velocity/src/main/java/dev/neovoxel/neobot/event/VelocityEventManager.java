package dev.neovoxel.neobot.event;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import dev.neovoxel.neobot.NeoBotVelocity;
import dev.neovoxel.neobot.adapter.VelocityPlayer;
import dev.neovoxel.neobot.game.event.PlayerEvent;

public class VelocityEventManager {
    private final NeoBotVelocity plugin;

    public VelocityEventManager(NeoBotVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onLogin(LoginEvent loginEvent) {
        plugin.getGameEventListener().onLogin(new VelocityLoginEvent(loginEvent));
    }

    @Subscribe
    public void onJoin(ServerConnectedEvent event) {
        plugin.getGameEventListener().onJoin(new PlayerEvent(new VelocityPlayer(event.getPlayer())));
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        plugin.getGameEventListener().onQuit(new PlayerEvent(new VelocityPlayer(event.getPlayer())));
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        plugin.getGameEventListener().onChat(new VelocityChatEvent(event));
    }
}
