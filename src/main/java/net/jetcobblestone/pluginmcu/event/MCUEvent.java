package net.jetcobblestone.pluginmcu.event;

import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.team.TeamPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MCUEvent {

    private final List<TeamPlayer> participants = new ArrayList<>();
    private final TeamManager teamManager;

    public MCUEvent(TeamManager teamManager) {
        this.teamManager = teamManager;

        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            for (String playerName : mcuTeam.getTeam().getEntries()) {
                final TeamPlayer teamPlayer = teamManager.getTeamPlayer(Objects.requireNonNull(Bukkit.getPlayer(playerName)));
                participants.add(teamPlayer);
            }
        }


    }

}
