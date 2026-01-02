package dev.neovoxel.neobot.extension;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.misc.EventListener;
import lombok.Getter;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExtensionsManager {
    PluginManager pluginManager;
    @Getter
    List<EventListener> listenerList = new ArrayList<>();
    public void loadExtensions(NeoBot plugin){
        // 初始化插件管理器
        PluginManager pluginManager = new DefaultPluginManager(Paths.get(plugin.getDataFolder().getAbsolutePath(),"plugins"));
        pluginManager.loadPlugins();

        // 启动插件
        pluginManager.startPlugins();

        // 获取所有实现了 ListenerProvider 的扩展
        List<ListenerProvider> extensions = pluginManager.getExtensions(ListenerProvider.class);
        for (ListenerProvider ext : extensions) {
            listenerList.add(ext.getListener());
        }
    }

    public void unloadExtensions(){
        for(EventListener listener : listenerList){
            listener.reset();
        }
        pluginManager.stopPlugins();
    }

}
