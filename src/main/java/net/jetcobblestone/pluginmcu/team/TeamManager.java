package net.jetcobblestone.pluginmcu.team;

import lombok.Getter;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TeamManager {
    @Getter private final Scoreboard teamBoard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
    @Getter private final List<MCUTeam> teamsList = new ArrayList<>();
    private final Map<Player, TeamPlayer> teamPlayerMap = new HashMap<>();

    protected void addTeamPlayer(TeamPlayer player) {
        teamPlayerMap.put(player.getPlayer(), player);
    }

    public TeamManager(TabManager tabManager) {
        final String[] colourStrings = ColourMapper.getColourNames();
        for (final String colour : colourStrings) {
            teamsList.add(new MCUTeam(colour, ColourMapper.colourFromString(colour), this, tabManager));
        }
    }

    public MCUTeam findTeam (String teamColour) {
        for (MCUTeam team : teamsList) {
            if (team.getColourName().equals(teamColour)) {
                return team;
            }
        }
        return null;
    }

    public TeamPlayer getTeamPlayer(Player player) {
        return teamPlayerMap.get(player);
    }
}
