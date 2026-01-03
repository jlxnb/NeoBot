package dev.neovoxel.neobot.bot;

import dev.neovoxel.nbapi.client.NBotClient;
import dev.neovoxel.nbapi.client.OBWSClient;
import dev.neovoxel.nbapi.client.OBWSServer;
import dev.neovoxel.neobot.NeoBot;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BotProvider {
    private Map<String, NBotClient> clients = new HashMap<>();
    private BotListener listener;
    private final NeoBot plugin;

    public BotProvider(NeoBot plugin) {
        this.plugin = plugin;
    }

    private void addBot(String type, NBotClient client) {
        clients.put(type, client);
    }

    private void setBotListener(BotListener listener) {
        this.listener = listener;
    }

    public Collection<NBotClient> getBot() {
        return clients.values();
    }

    public BotListener getBotListener() {
        return listener;
    }

    public void loadBot(NeoBot plugin) throws URISyntaxException {
        for (String type : plugin.getGeneralConfig().getStringArray("bot.type")) {
            if (type.equalsIgnoreCase("onebot11-ws")) {
                loadOnebot11Ws(plugin);
            } else if (type.equalsIgnoreCase("onebot11-ws-reverse")) {
                loadOnebot11WsReverse(plugin);
            }
        }
        plugin.submitAsync(() -> {
            for (NBotClient client : getBot()) {
                if (!client.isConnected()) client.reconnect();
            }
        }, 5, plugin.getGeneralConfig().getInt("bot.options.check-interval"));
    }

    public void loadOnebot11Ws(NeoBot plugin) throws URISyntaxException {
        String url = plugin.getGeneralConfig().getString("bot.onebot11-ws.url");
        URI uri = new URI(url);
        String token = plugin.getGeneralConfig().getString("bot.onebot11-ws.access-token");
        OBWSClient client;
        if (!token.isEmpty()) {
            client = new OBWSClient(uri, token);
        } else client = new OBWSClient(uri);
        addBot("onebot11-ws", client);
        setBotListener(new BotListener(plugin));
        client.addListener(getBotListener());
        client.connect();
    }

    public void loadOnebot11WsReverse(NeoBot plugin) {
        String address = plugin.getGeneralConfig().getString("bot.onebot11-ws-reverse.address");
        int port = plugin.getGeneralConfig().getInt("bot.onebot11-ws-reverse.port");
        String token = plugin.getGeneralConfig().getString("bot.onebot11-ws-reverse.access-token");
        OBWSServer client;
        if (!token.isEmpty()) {
            client = new OBWSServer(address, port, token);
        } else client = new OBWSServer(address, port);
        addBot("onebot11-ws-reverse", client);
        setBotListener(new BotListener(plugin));
        client.addListener(getBotListener());
        client.connect();
    }

    public void unloadBot() {
        getBot().forEach(NBotClient::disconnect);
    }

    public void reloadBot(NeoBot plugin) throws URISyntaxException {
        unloadBot();
        getBot().clear();
        loadBot(plugin);
    }
}
