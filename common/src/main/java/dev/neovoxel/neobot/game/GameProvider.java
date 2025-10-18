package dev.neovoxel.neobot.game;

import dev.neovoxel.neobot.util.Player;

public interface GameProvider {
    Player getOnlinePlayer(String name);

    Player[] getOnlinePlayers();

    void broadcast(String message);

    String parsePlaceholder(String message, Player player);
}
