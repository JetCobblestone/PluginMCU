package net.jetcobblestone.pluginmcu.team;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TeamListener implements Listener {

    private final TeamManager teamManager;

    public TeamListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        teamManager.registerPlayer(event.getPlayer());
    }

}
