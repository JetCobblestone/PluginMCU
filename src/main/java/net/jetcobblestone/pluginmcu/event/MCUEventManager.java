package net.jetcobblestone.pluginmcu.event;

import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.ScheduleManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MCUEventManager {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;
    private final ScheduleManager scheduleManager;

    private MCUEvent event;

    public MCUEventManager(JavaPlugin plugin, TeamManager teamManager, ScheduleManager scheduleManager) {
        this.teamManager = teamManager;
        this.scheduleManager = scheduleManager;
        this.plugin = plugin;
    }

    public boolean eventActive() {
        return event != null;
    }

    public void startEvent() {
        event = new MCUEvent(plugin, teamManager, scheduleManager);
    }
}
