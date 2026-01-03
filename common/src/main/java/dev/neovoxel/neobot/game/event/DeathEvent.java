package dev.neovoxel.neobot.game.event;

import dev.neovoxel.neobot.adapter.Player;
import lombok.Getter;
import org.graalvm.polyglot.HostAccess;

@Getter(onMethod_ = {@HostAccess.Export})
public class DeathEvent extends PlayerEvent {
    private final String message;

    public DeathEvent(Player player, String deathMessage) {
        super(player);
        this.message = deathMessage;
    }
}
