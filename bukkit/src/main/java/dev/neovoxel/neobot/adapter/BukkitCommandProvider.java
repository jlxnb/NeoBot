package dev.neovoxel.neobot.adapter;

import dev.neovoxel.neobot.NeoBot;
import dev.neovoxel.neobot.NeoBotBukkit;
import dev.neovoxel.neobot.command.CommandProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BukkitCommandProvider extends CommandProvider implements CommandExecutor {
    private final NeoBotBukkit plugin;

    public BukkitCommandProvider(NeoBotBukkit plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void registerCommand() {
        plugin.getCommand("neobot").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.onCommand(new BukkitCommandSender(commandSender), strings);
        return false;
    }
}
