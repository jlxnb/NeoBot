package dev.neovoxel.neobot.config;

import dev.neovoxel.neobot.NeoBot;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;

public interface ConfigProvider {
    default void loadConfig(NeoBot plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        loadGeneralConfig(plugin);
        loadMessageConfig(plugin);
    }

    default void reloadConfig(NeoBot plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        loadGeneralConfig(plugin);
        getMessageConfig().flush(plugin);
    }

    void setMessageConfig(EnhancedConfig config);

    EnhancedConfig getMessageConfig();

    void setGeneralConfig(EnhancedConfig config);

    EnhancedConfig getGeneralConfig();

    default void loadGeneralConfig(NeoBot plugin) {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.json");
            if (!configFile.exists()) saveResource(plugin.getDataFolder(), "config.json");
            setGeneralConfig(new EnhancedConfig(configFile, new JSONObject(new String(Files.readAllBytes(configFile.toPath())))));
        } catch (Exception e) {
            plugin.getNeoLogger().error("Failed to release the general config file", e);
        }
    }

    default void loadMessageConfig(NeoBot plugin) {
        try {
            File messageFile = new File(plugin.getDataFolder(), "messages.json");
            if (!messageFile.exists()) saveResource(plugin.getDataFolder(), "messages.json");
            setMessageConfig(new EnhancedConfig(messageFile, new JSONObject(new String(Files.readAllBytes(messageFile.toPath())))));
        } catch (Exception e) {
            plugin.getNeoLogger().error("Failed to release the messages config file", e);
        }
    }

    default void saveResource(File parent, String fileName) throws IOException {
        File file = new File(parent, fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        if (!file.exists()) file.createNewFile();
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(builder.toString());
        writer.close();
    }
}
