package dev.neovoxel.neobot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.graalvm.polyglot.HostAccess;
import org.json.JSONArray;
import org.json.JSONObject;

@Data
@AllArgsConstructor
public class Config {
    protected JSONObject jsonObject;

    @HostAccess.Export
    public String getString(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getString(nodes[nodes.length - 1]);
    }

    @HostAccess.Export
    public int getInt(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getInt(nodes[nodes.length - 1]);
    }

    @HostAccess.Export
    public double getDouble(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getDouble(nodes[nodes.length - 1]);
    }

    @HostAccess.Export
    public boolean getBoolean(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getBoolean(nodes[nodes.length - 1]);
    }

    public JSONObject getJSONObject(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getJSONObject(nodes[nodes.length - 1]);
    }

    public JSONArray getJSONArray(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return newObj.getJSONArray(nodes[nodes.length - 1]);
    }

    @HostAccess.Export
    public Config[] getArray(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        JSONArray array = newObj.getJSONArray(nodes[nodes.length - 1]);
        Config[] configs = new Config[array.length()];
        for (int i = 0; i < array.length(); i++) {
            configs[i] = new Config(array.getJSONObject(i));
        }
        return configs;
    }

    @HostAccess.Export
    public String[] getStringArray(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        JSONArray array = newObj.getJSONArray(nodes[nodes.length - 1]);
        String[] strings = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            strings[i] = array.getString(i);
        }
        return strings;
    }

    @HostAccess.Export
    public Config getObject(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        return new Config(newObj.getJSONObject(nodes[nodes.length - 1]));
    }

    @HostAccess.Export
    public boolean has(String node) {
        try {
            JSONObject newObj = jsonObject;
            String[] nodes = node.split("\\.");
            for (int i = 0; i < nodes.length - 1; i++) {
                newObj = newObj.getJSONObject(nodes[i]);
            }
            return newObj.has(nodes[nodes.length - 1]);
        } catch (Exception ignored) {
            return false;
        }
    }

    @HostAccess.Export
    public void put(String node, Object value) {
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            if (!jsonObject.has(nodes[i])) {
                jsonObject.put(nodes[i], new JSONObject());
            }
        }
        jsonObject.put(nodes[nodes.length - 1], value);
    }
}
