package net.jetcobblestone.pluginmcu.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BoardListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PacketBoard board = PacketBoard.getPlayerMap().get(player.getUniqueId());
        if (board != null) {
            board.initPlayer(player);
        }
    }
}
