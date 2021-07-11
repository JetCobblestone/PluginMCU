package net.jetcobblestone.pluginmcu;

import net.jetcobblestone.pluginmcu.commands.JoinTeam;
import net.jetcobblestone.pluginmcu.commands.LeaveTeam;
import net.jetcobblestone.pluginmcu.commands.RenameTeam;
import net.jetcobblestone.pluginmcu.commands.StartEvent;
import net.jetcobblestone.pluginmcu.commands.tabs.JoinTeamTab;
import net.jetcobblestone.pluginmcu.event.EventManager;
import net.jetcobblestone.pluginmcu.tab.TabListener;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import net.jetcobblestone.pluginmcu.team.TeamListener;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class PluginMCU extends JavaPlugin {

    private TeamManager teamManager;
    private TabManager tabManager;
    private EventManager eventManager;

    @Override
    public void onEnable() {
        tabManager = new TabManager();
        teamManager = new TeamManager(tabManager);
        tabManager.init(teamManager);
        eventManager = new EventManager(teamManager);

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        tabManager.resetTab();
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
