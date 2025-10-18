package dev.neovoxel.neobot.adapter;

import dev.neovoxel.neobot.util.CommandSender;

public class BukkitCommandSender extends CommandSender {
    private final org.bukkit.command.CommandSender sender;

    public BukkitCommandSender(org.bukkit.command.CommandSender sender) {
        super(sender.getName());
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String node) {
        return sender.hasPermission(node);
    }
}
