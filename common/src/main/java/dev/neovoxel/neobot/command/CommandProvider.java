package dev.neovoxel.neobot.command;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.util.CommandSender;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandProvider {
    private final List<String> helpMessages = new ArrayList<>();
    private final List<Value> methods = new ArrayList<>();

    protected final NeoBot plugin;

    protected CommandProvider(NeoBot plugin) {
        this.plugin = plugin;
        JSONArray jsonArray = plugin.getMessageConfig().getJSONArray("internal.help");
        for (int i = 0; i < jsonArray.length(); i++) {
            helpMessages.add(jsonArray.getString(i));
        }
    }

    public abstract void registerCommand();

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("neobot.command.help")) {
                for (String message : helpMessages) {
                    sender.sendMessage(message);
                }
            } else sender.sendMessage(plugin.getMessageConfig().getMessage("internal.no-permission"));
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("neobot.command.help")) {
                    for (String message : helpMessages) {
                        sender.sendMessage(message);
                    }
                } else sender.sendMessage(plugin.getMessageConfig().getMessage("internal.no-permission"));
                return;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("neobot.command.reload")) {
                    sender.sendMessage(plugin.getMessageConfig().getMessage("internal.reload.reloading"));
                    plugin.reload(sender);
                } else sender.sendMessage(plugin.getMessageConfig().getMessage("internal.no-permission"));
                return;
            }
        }
        methods.forEach(method -> method.execute(sender, args));
    }

    @HostAccess.Export
    public void addHelpMessage(String message) {
        helpMessages.add(message);
    }

    @HostAccess.Export
    public void onCommand(Value value) {
        if (value.canExecute()) {
            methods.add(value);
        }
    }
}
