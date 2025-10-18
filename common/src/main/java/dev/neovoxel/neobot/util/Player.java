package dev.neovoxel.neobot.util;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class Player {
    private final String name;
    private final UUID uuid;

    protected Player(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public abstract void sendMessage(String message);

    public abstract void kick(String message);

    public abstract boolean hasPermission(String node);
}
