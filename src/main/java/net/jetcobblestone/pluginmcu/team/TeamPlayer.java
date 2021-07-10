package net.jetcobblestone.pluginmcu.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class TeamPlayer {
    @Getter private final Player player;
    @Setter @Getter private MCUTeam team;

    protected TeamPlayer(Player player, TeamManager teamManager) {
        this.player = player;
        teamManager.addTeamPlayer(this);
    }
}
