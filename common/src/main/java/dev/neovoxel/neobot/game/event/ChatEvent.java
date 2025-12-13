package dev.neovoxel.neobot.game.event;

import dev.neovoxel.neobot.adapter.Player;
import lombok.Getter;
import org.graalvm.polyglot.HostAccess;

@Getter(onMethod_ = {@HostAccess.Export})
public abstract class ChatEvent {
    private final Player player;
    private final String message;

    protected ChatEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    @HostAccess.Export
    public abstract void disallow();
}
