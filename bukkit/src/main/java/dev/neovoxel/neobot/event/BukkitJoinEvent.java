package dev.neovoxel.neobot.event;

import dev.neovoxel.neobot.game.event.JoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class BukkitJoinEvent extends JoinEvent {
    private final PlayerLoginEvent event;

    public BukkitJoinEvent(PlayerLoginEvent event) {
        super(event.getPlayer().getName(), event.getPlayer().getUniqueId());
        this.event = event;
    }

    @Override
    public void disallow(String reason) {
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, reason);
    }
}
