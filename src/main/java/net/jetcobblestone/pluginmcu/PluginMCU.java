package net.jetcobblestone.pluginmcu;

import net.jetcobblestone.pluginmcu.commands.JoinTeam;
import net.jetcobblestone.pluginmcu.commands.LeaveTeam;
import net.jetcobblestone.pluginmcu.commands.RenameTeam;
import net.jetcobblestone.pluginmcu.commands.StartEvent;
import net.jetcobblestone.pluginmcu.commands.tabs.JoinTeamTab;
import net.jetcobblestone.pluginmcu.event.MCUEventManager;
import net.jetcobblestone.pluginmcu.tab.TabListener;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import net.jetcobblestone.pluginmcu.team.TeamListener;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.ScheduleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;

@SuppressWarnings("unused")
public class PluginMCU extends JavaPlugin {

    private ScheduleManager scheduleManager;
    private TeamManager teamManager;
    private TabManager tabManager;
    private MCUEventManager eventManager;

    @Override
    public void onEnable() {
        scheduleManager = new ScheduleManager(this);
        tabManager = new TabManager();
        teamManager = new TeamManager(tabManager);
        tabManager.init(teamManager);
        eventManager = new MCUEventManager(this, teamManager, scheduleManager);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        Objective obj = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("sidebar");
        if (obj != null) obj.unregister();
    }

    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        getCommand("JoinTeam").setExecutor(new JoinTeam(teamManager, eventManager));
        getCommand("JoinTeam").setTabCompleter(new JoinTeamTab());
        getCommand("RenameTeam").setExecutor(new RenameTeam(teamManager, eventManager));
        getCommand("LeaveTeam").setExecutor(new LeaveTeam(teamManager, eventManager));
        getCommand("StartEvent").setExecutor(new StartEvent(eventManager));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TabListener(this, tabManager), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamManager), this);
    }
}
