package dev.neovoxel.neobot.script;

import dev.neovoxel.jarflow.JarFlow;
import dev.neovoxel.neobot.NeoBot;
import lombok.Getter;
import lombok.Setter;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ScriptProvider {
    @Getter
    @Setter
    private boolean scriptSystemLoaded = false;

    private final List<Script> scripts = new ArrayList<>();

    private final List<Value> placeholderParsers = new ArrayList<>();
    
    private final Map<String, Value> methods = new HashMap<>();

    private final NeoBot plugin;

    public ScriptProvider(NeoBot plugin) {
        this.plugin = plugin;
    }

    int pluginSchemaVersion = 1;

    private final List<Context> contexts = new ArrayList<>();

    private static final Engine engine;

    private static final List<Class> exposed = new ArrayList<>();

    private static final HostAccess hostAccess;

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
        try {
            exposed.addAll(JarFlow.searchClasses("dev.neovoxel.nsapi"));
            exposed.addAll(JarFlow.searchClasses("dev.neovoxel.nbapi.action"));
            exposed.addAll(JarFlow.searchClasses("dev.neovoxel.nbapi.event"));
            exposed.addAll(JarFlow.searchClasses("dev.neovoxel.nbapi.util"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        HostAccess.Builder builder1 = HostAccess.newBuilder(HostAccess.EXPLICIT);
        builder1.allowAccessAnnotatedBy(HostAccess.Export.class);
        builder1.allowListAccess(true);
        builder1.allowArrayAccess(true);
        exposed.add(Object.class);
        exposed.add(Enum.class);
        for (Class<?> clazz : exposed) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals("wait") || method.getName().equals("notify") || method.getName().equals("notifyAll")) {
                    continue; // 跳过敏感方法
                }
                builder1.allowAccess(method);
            }
        }
        hostAccess = builder1.build();
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
                .allowHostAccess(hostAccess)
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
