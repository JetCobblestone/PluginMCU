package net.jetcobblestone.pluginmcu.tab;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TabListener implements Listener {

    private final TabManager tabManager;
    private final JavaPlugin plugin;

    public TabListener(JavaPlugin plugin, TabManager tabManager) {
        this.plugin = plugin;
        this.tabManager = tabManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, tabManager::updateTab, 1L);
    }

}
