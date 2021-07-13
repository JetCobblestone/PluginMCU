package net.jetcobblestone.pluginmcu.team;

import lombok.Getter;
import net.jetcobblestone.pluginmcu.tab.OrderCounter;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TeamManager {
    @Getter private final Scoreboard teamBoard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    @Getter private final List<MCUTeam> teamsList = new ArrayList<>();
    private final Map<UUID, TeamPlayer> teamPlayerMap = new HashMap<>();

    public TeamManager(TabManager tabManager) {
        final String[] colourStrings = ColourMapper.getColourNames();
        final OrderCounter orderCounter = new OrderCounter(2);
        orderCounter.inc(1);

        for (int i = 0; i < colourStrings.length; i++) {
            final String colour = colourStrings[i];
            teamsList.add(new MCUTeam(colour, ColourMapper.colourFromString(colour), this, tabManager, orderCounter.get()));
            orderCounter.inc(4);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            registerPlayer(player);
        }
    }

    protected void registerPlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        if (teamPlayerMap.get(uuid) == null) {
            teamPlayerMap.put(uuid, new TeamPlayer(player));
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
        return teamPlayerMap.get(player.getUniqueId());
    }
}
