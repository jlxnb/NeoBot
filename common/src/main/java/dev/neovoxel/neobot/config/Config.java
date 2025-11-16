package dev.neovoxel.neobot.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.graalvm.polyglot.HostAccess;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

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
    public long[] getNumberArray(String node) {
        JSONObject newObj = jsonObject;
        String[] nodes = node.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            newObj = newObj.getJSONObject(nodes[i]);
        }
        JSONArray array = newObj.getJSONArray(nodes[nodes.length - 1]);
        long[] numbers = new long[array.length()];
        for (int i = 0; i < array.length(); i++) {
            numbers[i] = array.getLong(i);
        }
        return numbers;
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
    public String[] getKeys() {
        return jsonObject.keySet().toArray(new String[0]);
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
        if (node == null || node.isEmpty()) {
            return;
        }
        String[] nodes = node.split("\\.");
        if (nodes.length == 0) {
            return;
        }
        JSONObject currentJson = jsonObject;
        for (int i = 0; i < nodes.length - 1; i++) {
            String currentNode = nodes[i];
            if (currentNode.isEmpty()) {
                return;
            }
            if (!currentJson.has(currentNode) || !(currentJson.get(currentNode) instanceof JSONObject)) {
                currentJson.put(currentNode, new JSONObject());
            }
            currentJson = currentJson.getJSONObject(currentNode);
        }
        String lastNode = nodes[nodes.length - 1];
        if (!lastNode.isEmpty()) {
            currentJson.put(lastNode, convertPolyglotValue(value));
        }
    }

    protected Object convertPolyglotValue(Object value) {
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            JSONArray arr = new JSONArray();
            for (Object o : list) {
                arr.put(convertPolyglotValue(o));
            }
            return arr;
        }
        if (value instanceof Map<?,?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            JSONObject obj = new JSONObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                obj.put(entry.getKey().toString(), convertPolyglotValue(entry.getValue()));
            }
            return obj;
        }
        return value;
    }
}
