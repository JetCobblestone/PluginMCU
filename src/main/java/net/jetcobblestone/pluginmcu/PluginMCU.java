package net.jetcobblestone.pluginmcu;

import net.jetcobblestone.pluginmcu.commands.JoinTeam;
import net.jetcobblestone.pluginmcu.commands.LeaveTeam;
import net.jetcobblestone.pluginmcu.commands.RenameTeam;
import net.jetcobblestone.pluginmcu.commands.StartEvent;
import net.jetcobblestone.pluginmcu.commands.tabs.JoinTeamTab;
import net.jetcobblestone.pluginmcu.event.MCUEventManager;
import net.jetcobblestone.pluginmcu.scoreboard.BoardListener;
import net.jetcobblestone.pluginmcu.scoreboard.PacketBoard;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import net.jetcobblestone.pluginmcu.team.TeamListener;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.ScheduleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public class PluginMCU extends JavaPlugin {

    private ScheduleManager scheduleManager;
    private TabManager tabManager;
    private TeamManager teamManager;
    private MCUEventManager eventManager;

    @Override
    public void onEnable() {
        scheduleManager = new ScheduleManager(this);
        tabManager = new TabManager(this);
        teamManager = new TeamManager(tabManager);
        eventManager = new MCUEventManager(this, teamManager, scheduleManager);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        tabManager.setSize(0);

        for (Map.Entry<UUID, PacketBoard> entry : PacketBoard.getPlayerMap().entrySet()) {
            entry.getValue().removePlayer(Objects.requireNonNull(Bukkit.getPlayer(entry.getKey())));
        }
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
        getServer().getPluginManager().registerEvents(tabManager, this);
       // getServer().getPluginManager().registerEvents(new BoardListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamManager), this);
    }
}
