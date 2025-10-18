package dev.neovoxel.neobot.adapter;

import dev.neovoxel.neobot.scheduler.ScheduledTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class BukkitScheduledTask implements ScheduledTask {
    private final BukkitTask task;
    private static final List<BukkitTask> tasks = new ArrayList<>();

    public BukkitScheduledTask(BukkitTask task) {
        this.task = task;
        tasks.add(task);
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    public static void cancelAll() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }
}
