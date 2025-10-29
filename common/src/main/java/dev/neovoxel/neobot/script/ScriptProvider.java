package dev.neovoxel.neobot.script;

import com.oracle.truffle.polyglot.PolyglotImpl;
import dev.neovoxel.jarflow.JarFlow;
import dev.neovoxel.jarflow.dependency.Dependency;
import dev.neovoxel.neobot.NeoBot;
import lombok.Getter;
import lombok.Setter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Context context;

    public void addLoadedScript(Script script) {
        scripts.add(script);
    }

    public boolean isScriptLoaded(Script script) {
        return scripts.contains(script);
    }

    public void loadScript(NeoBot plugin) throws Throwable {
        plugin.getNeoLogger().info("Loading scripts...");
        File jsLog = new File("engine.log");
        if (!jsLog.exists()) jsLog.createNewFile();
        OutputStream stream = new FileOutputStream(jsLog);
        Engine engine = Engine.newBuilder()
                .allowExperimentalOptions(true)
                .option("engine.WarnInterpreterOnly", "false")
                .logHandler(stream)
                .build();
        context = Context.newBuilder("js")
                .allowIO(true)
                .engine(engine)
                .build();
        context.getBindings("js").putMember("qq", plugin.getBotProvider().getBotListener());
        context.getBindings("js").putMember("plugin", plugin);
        context.getBindings("js").putMember("gameEvent", plugin.getGameEventListener());
        context.getBindings("js").putMember("gameCommand", plugin.getCommandProvider());
        context.getBindings("js").putMember("messageConfig", plugin.getMessageConfig());
        context.getBindings("js").putMember("generalConfig", plugin.getGeneralConfig());
        context.getBindings("js").putMember("scriptManager", this);
        File scriptPath = new File(plugin.getDataFolder(), "scripts");
        if (!scriptPath.exists()) {
            scriptPath.mkdirs();
        }
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
                    jsonObject.getString("entrypoint")
            );
            if (jsonObject.has("description")) {
                script.setDescription(jsonObject.getString("description"));
            }
            File jsFile = new File(file, jsonObject.getString("entrypoint"));
            if (!jsFile.exists()) {
                plugin.getNeoLogger().warn("The script " + script.getName() + " does not have a entrypoint file " + jsFile.getName());
                continue;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsFile), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            context.eval("js", builder.toString());
            addLoadedScript(script);
            plugin.getNeoLogger().info("Loaded script " + script.getName());
        }
        scriptSystemLoaded = true;
    }

    public void unloadScript() {
        context.close();
        context = null;
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
