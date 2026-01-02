package dev.neovoxel.neobot.extension;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.misc.EventListener;

import java.util.List;

public interface PluginsProvider {
    List<EventListener> getListeners();
    void loadPlugins(NeoBot plugin);
    void unloadPlugins();
}
