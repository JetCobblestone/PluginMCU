package net.jetcobblestone.pluginmcu;

import net.jetcobblestone.pluginmcu.commands.JoinTeam;
import net.jetcobblestone.pluginmcu.commands.RenameTeam;
import net.jetcobblestone.pluginmcu.commands.StartEvent;
import net.jetcobblestone.pluginmcu.commands.tabs.JoinTeamTab;
import net.jetcobblestone.pluginmcu.tab.TabListener;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMCU extends JavaPlugin {

    private TeamManager teamManager;
    private TabManager tabManager;

    @Override
    public void onEnable() {
        tabManager = new TabManager();
        teamManager = new TeamManager(tabManager);
        tabManager.init(teamManager);
        registerCommands();
        getServer().getPluginManager().registerEvents(new TabListener(this, tabManager), this);
    }

    @Override
    public void onDisable() {
        tabManager.resetTab();
    }

    private void registerCommands() {
        getCommand("JoinTeam").setExecutor(new JoinTeam(teamManager));
        getCommand("JoinTeam").setTabCompleter(new JoinTeamTab());
        getCommand("RenameTeam").setExecutor(new RenameTeam(teamManager));
        getCommand("StartEvent").setExecutor(new StartEvent(tabManager));
    }
}
