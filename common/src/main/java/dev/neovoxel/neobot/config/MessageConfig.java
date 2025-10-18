package dev.neovoxel.neobot.config;

import dev.neovoxel.neobot.NeoBot;
import org.graalvm.polyglot.HostAccess;
import org.h2.util.json.JSONValue;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class MessageConfig extends Config {
    private final File file;
    private final Map<String, Object> needFlushOptions = new HashMap<>();

    public MessageConfig(File file, JSONObject jsonObject) {
        super(new JSONObject(jsonObject.toString().replace("&", "§")));
        this.file = file;
    }

    @HostAccess.Export
    public String getMessage(String node) {
        return (super.getString("prefix") + super.getString(node));
    }

    @HostAccess.Export
    public void addOption(String node, Object defaultValue) {
        if (!super.has(node)) {
            super.put(node, defaultValue);
            needFlushOptions.put(node, defaultValue);
        }
    }

    public void flush(NeoBot plugin) {
        try {
            String originContent = new String(Files.readAllBytes(file.toPath()));
            JSONObject jsonObject = new JSONObject(originContent);
            Config config = new Config(jsonObject);
            for (Map.Entry<String, Object> entry : needFlushOptions.entrySet()) {
                config.put(entry.getKey(), entry.getValue());
            }
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(config.getJsonObject().toString());
            writer.close();
            needFlushOptions.clear();
            super.jsonObject = new JSONObject(config.getJsonObject().toString().replace("&", "§"));
        } catch (IOException e) {
            plugin.getNeoLogger().error("Failed to flush the messages config", e);
        }
    }
}
