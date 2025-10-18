package dev.neovoxel.neobot.event;

import dev.neovoxel.neobot.adapter.BukkitPlayer;
import dev.neovoxel.neobot.game.event.ChatEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class BukkitChatEvent extends ChatEvent {

    private final AsyncPlayerChatEvent event;

    public BukkitChatEvent(AsyncPlayerChatEvent event) {
        super(new BukkitPlayer(event.getPlayer()), event.getMessage());
        this.event = event;
    }

    @Override
    public void disallow() {
        event.setCancelled(true);
    }
}
