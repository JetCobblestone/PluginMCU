package net.jetcobblestone.pluginmcu.team;

import lombok.Getter;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class MCUTeam{
    private final List<TeamPlayer> players = new ArrayList<>();
    private final TeamManager teamManager;
    private final TabManager tabManager;
    @Getter private final String colourName;
    @Getter private String displayName;
    @Getter private final Team team;
    @Getter private final String teamName;

    public MCUTeam(String colourName, ChatColor colour, TeamManager teamManager, TabManager tabManager, String teamName) {
        this.colourName = colourName;
        this.teamManager = teamManager;
        this.team = teamManager.getTeamBoard().registerNewTeam(teamName);
        this.tabManager = tabManager;
        this.teamName = teamName;
        team.setColor(colour);
        displayName = team.getColor() + "" + ChatColor.BOLD + colourName;
    }

    public void setDisplayName(String name) {
        displayName = team.getColor() + "" + ChatColor.BOLD + name;
    }

    public void addPlayer(Player player) {
        if (players.size() >= 3) {
            player.sendMessage(ChatColor.RED + "This team is full!");
            return;
        }

        final TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);

        if (teamPlayer.getTeam() == this){
            player.sendMessage(ChatColor.RED + "You are already on this team");
            return;
        }
        else {
            if (teamPlayer.getTeam() != null) {
                teamPlayer.getTeam().removePlayer(teamPlayer);
            }
        }

        players.add(teamPlayer);
        team.addEntry(teamPlayer.getPlayer().getDisplayName());
        teamPlayer.setTeam(this);
        player.sendMessage(ChatColor.GOLD + "You joined team " + displayName);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removePlayer(TeamPlayer teamPlayer) {
        if (players.contains(teamPlayer)) {
            players.remove(teamPlayer);
            team.removeEntry(teamPlayer.getPlayer().getName());
            teamPlayer.setTeam(null);
        }
    }

}
