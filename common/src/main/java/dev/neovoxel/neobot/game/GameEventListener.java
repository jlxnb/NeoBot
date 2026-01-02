package dev.neovoxel.neobot.game;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.game.event.ChatEvent;
import dev.neovoxel.neobot.game.event.LoginEvent;
import dev.neovoxel.neobot.adapter.Player;
import dev.neovoxel.neobot.misc.EventListener;

public class GameEventListener extends EventListener {
    public GameEventListener(NeoBot plugin) {
        super(plugin);
    }

    public void onLogin(LoginEvent event) {
        fireEvent("LoginEvent", event);
    }

    public void onJoin(Player player) {
        fireEvent("JoinEvent", player);
    }


    public void onQuit(Player player) {
        fireEvent("QuitEvent", player);
    }

    public void onDeath(Player player, String message) {
        fireEvent("DeathEvent", player, message);
    }

    public void onChat(ChatEvent event) {
        fireEvent("ChatEvent", event);
    }

    public void onPluginEnable() {
        fireEvent("PluginEnableEvent");
    }

    public void onPluginDisable() {
        fireEvent("PluginDisableEvent");
    }

    public void onPrePluginReload() {
        fireEvent("PrePluginReloadEvent");
    }

    public void onPluginReloaded() {
        fireEvent("PluginReloadedEvent");
    }
}
