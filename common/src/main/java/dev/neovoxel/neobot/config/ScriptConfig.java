package dev.neovoxel.neobot.config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class ScriptConfig extends EnhancedConfig {
    private final EnhancedConfig generalConfig;

    public ScriptConfig(File file, JSONObject jsonObject, EnhancedConfig generalConfig) {
        super(file, jsonObject);
        this.generalConfig = generalConfig;
    }

    @Override
    public String getString(String node) {
        try {
            return super.getString(node);
        } catch (Exception ignored) {
            return generalConfig.getString(node);
        }
    }

    @Override
    public boolean getBoolean(String node) {
        try {
            return super.getBoolean(node);
        } catch (Exception ignored) {
            return generalConfig.getBoolean(node);
        }
    }

    @Override
    public Config getObject(String node) {
        try {
            return super.getObject(node);
        } catch (Exception ignored) {
            return generalConfig.getObject(node);
        }
    }

    @Override
    public String[] getStringArray(String node) {
        try {
            return super.getStringArray(node);
        } catch (Exception ignored) {
            return generalConfig.getStringArray(node);
        }
    }

    @Override
    public Config[] getArray(String node) {
        try {
            return super.getArray(node);
        } catch (Exception ignored) {
            return generalConfig.getArray(node);
        }
    }

    @Override
    public double getDouble(String node) {
        try {
            return super.getDouble(node);
        } catch (Exception ignored) {
            return generalConfig.getDouble(node);
        }
    }

    @Override
    public int getInt(String node) {
        try {
            return super.getInt(node);
        } catch (Exception ignored) {
            return generalConfig.getInt(node);
        }
    }

    @Override
    public JSONArray getJSONArray(String node) {
        try {
            return super.getJSONArray(node);
        } catch (Exception ignored) {
            return generalConfig.getJSONArray(node);
        }
    }

    @Override
    public JSONObject getJSONObject(String node) {
        try {
            return super.getJSONObject(node);
        } catch (Exception ignored) {
            return generalConfig.getJSONObject(node);
        }
    }

    @Override
    public String[] getKeys() {
        try {
            return super.getKeys();
        } catch (Exception ignored) {
            return generalConfig.getKeys();
        }
    }

    @Override
    public long[] getNumberArray(String node) {
        try {
            return super.getNumberArray(node);
        } catch (Exception ignored) {
            return generalConfig.getNumberArray(node);
        }
    }

    @Override
    public String getMessage(String node) {
        try {
            return super.getMessage(node);
        } catch (Exception ignored) {
            return generalConfig.getMessage(node);
        }
    }
}
