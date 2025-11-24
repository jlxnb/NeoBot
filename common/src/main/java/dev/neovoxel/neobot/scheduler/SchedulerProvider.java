package dev.neovoxel.neobot.scheduler;

import dev.neovoxel.neobot.script.ScriptProvider;
import org.graalvm.polyglot.HostAccess;

public interface SchedulerProvider {
    @HostAccess.Export
    ScheduledTask submit(Runnable task);

    @HostAccess.Export
    ScheduledTask submitAsync(Runnable task);

    @HostAccess.Export
    ScheduledTask submit(Runnable task, long delay);

    @HostAccess.Export
    ScheduledTask submitAsync(Runnable task, long delay);

    @HostAccess.Export
    ScheduledTask submit(Runnable task, long delay, long period);

    @HostAccess.Export
    ScheduledTask submitAsync(Runnable task, long delay, long period);

    @HostAccess.Export
    ScheduledTask submit(String scriptName, String functionName, long delay);

    @HostAccess.Export
    ScheduledTask submitAsync(String scriptName, String functionName, long delay);

    @HostAccess.Export
    ScheduledTask submit(String scriptName, String functionName, long delay, long period);

    @HostAccess.Export
    ScheduledTask submitAsync(String scriptName, String functionName, long delay, long period);

    void cancelAllTasks();
}
