package dev.neovoxel.neobot.script;

import dev.neovoxel.neobot.NeoBot;
import lombok.Getter;
import lombok.Setter;
import org.graalvm.polyglot.*;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ScriptProvider {
    @Getter
    @Setter
    private boolean scriptSystemLoaded = false;

    private List<Script> scripts = new ArrayList<>();

    private List<Value> placeholderParsers = new ArrayList<>();
    
    private Map<String, Value> methods = new HashMap<>();

    private final NeoBot plugin;

    public ScriptProvider(NeoBot plugin) {
        this.plugin = plugin;
    }

    int pluginSchemaVersion = 1;

    private List<Context> contexts = new ArrayList<>();

    private static final Engine engine;

    static {
        OutputStream stream = null;
        try {
            File jsLog = new File("engine.log");
            if (!jsLog.exists()) jsLog.createNewFile();
            stream = new FileOutputStream(jsLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Engine.Builder builder = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("engine.WarnInterpreterOnly", "false");
        if (stream != null) {
            builder.logHandler(stream);
        }
        engine = builder.build();
    }

    public void addLoadedScript(Script script) {
        scripts.add(script);
    }

    public boolean isScriptLoaded(Script script) {
        return scripts.contains(script);
    }

    public void loadScript(NeoBot plugin) throws Throwable {
        plugin.getNeoLogger().info("Loading scripts...");
        File scriptPath = new File(plugin.getDataFolder(), "scripts");
        if (!scriptPath.exists()) {
            scriptPath.mkdirs();
        }
        Set<Script> unsortedScripts = new HashSet<>();
        for (File file : scriptPath.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }
            File manifest = new File(file, "manifest.json");
            if (!manifest.exists()) {
                continue;
            }
            JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(manifest.toPath())));
            int schemaVersion = jsonObject.getInt("schema_version");
            if (schemaVersion > pluginSchemaVersion) {
                plugin.getNeoLogger().warn("The script " + file.getName() + " is using a newer schema version than the current one. Please update the plugin.");
                continue;
            }
            Script script = new Script(
                    schemaVersion,
                    jsonObject.getString("name"),
                    jsonObject.getString("author"),
                    jsonObject.getString("version"),
                    new File(file, jsonObject.getString("entrypoint"))
            );
            if (jsonObject.has("description")) {
                script.setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.has("loadbefore")) {
                for (Object object : jsonObject.getJSONArray("loadbefore")) {
                    script.getLoadbefore().add(object.toString());
                }
            }
            if (jsonObject.has("loadafter")) {
                for (Object object : jsonObject.getJSONArray("loadafter")) {
                    script.getLoadafter().add(object.toString());
                }
            }
            if (jsonObject.has("depends")) {
                for (Object object : jsonObject.getJSONArray("depends")) {
                    script.getDepends().add(object.toString());
                }
            }
            unsortedScripts.add(script);
        }
        List<Script> sortedScripts = Script.sortScripts(unsortedScripts);
        List<Script> checkedScripts = new ArrayList<>();
        for (Script script : sortedScripts) {
            if (!script.checkDepends(sortedScripts)) {
                plugin.getNeoLogger().warn("The script " + script.getName() + " is missing dependencies, it needs: " +
                        Arrays.toString(script.getDepends().toArray()));
            } else {
                checkedScripts.add(script);
            }
        }
        for (Script script : checkedScripts) {
            loadScript(plugin, script);
        }
        scriptSystemLoaded = true;
    }

    public void loadScript(NeoBot plugin, Script script) throws Throwable {
        if (!script.getEntrypoint().exists()) {
            plugin.getNeoLogger().warn("The script " + script.getName() + " is missing the entrypoint file.");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(script.getEntrypoint()), StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        Context context = Context.newBuilder("js")
                .allowIO(true)
                .allowAllAccess(true)
                .allowCreateThread(true)
                .engine(engine)
                .build();
        context.getBindings("js").putMember("qq", plugin.getBotProvider().getBotListener());
        context.getBindings("js").putMember("plugin", plugin);
        context.getBindings("js").putMember("gameEvent", plugin.getGameEventListener());
        context.getBindings("js").putMember("gameCommand", plugin.getCommandProvider());
        context.getBindings("js").putMember("messageConfig", plugin.getMessageConfig());
        context.getBindings("js").putMember("generalConfig", plugin.getScriptConfig());
        context.getBindings("js").putMember("scriptManager", this);
        context.eval("js", builder.toString());
        addLoadedScript(script);
        plugin.getNeoLogger().info("Loaded script " + script.getName());
    }

    public void unloadScript() {
        contexts.forEach(Context::close);
        contexts.clear();
        scripts.clear();
        placeholderParsers.clear();
        methods.clear();
    }

    public void downloadDefault() {

    }

    @HostAccess.Export
    public void loadParser(Value value) {
        if (value.canExecute()) {
            placeholderParsers.add(value);
        }
    }

    @HostAccess.Export
    public String parse(String content) {
        for (Value value : placeholderParsers) {
            content = value.execute(content).asString();
        }
        return content;
    }

    @HostAccess.Export
    public boolean isScriptLoaded(String name) {
        return scripts.stream().anyMatch(script -> script.getName().equals(name));
    }

    @HostAccess.Export
    public void addJsMethod(String name, Value value) {
        methods.put(name, value);
    }

    @HostAccess.Export
    public boolean hasJsMethod(String name) {
        return methods.containsKey(name);
    }

    @HostAccess.Export
    public Value callJsMethod(String name, Object... args) {
        return methods.get(name).execute(args);
    }
}
