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

    public MCUTeam(String colourName, ChatColor colour, TeamManager teamManager, TabManager tabManager) {
        this.colourName = colourName;
        this.teamManager = teamManager;
        this.team = teamManager.getTeamBoard().registerNewTeam(colourName);
        this.tabManager = tabManager;
        team.setColor(colour);
        displayName = team.getColor() + "" + ChatColor.BOLD + colourName;
    }

    public void setDisplayName(String name) {
        displayName = team.getColor() + "" + ChatColor.BOLD + name;
        tabManager.updateTab();
    }

    public void addPlayer(Player player, TeamManager teamManager) {
        if (players.size() >= 3) {
            player.sendMessage(ChatColor.RED + "This team is full!");
            return;
        }

        TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);
        if (teamPlayer == null) {
            teamPlayer = new TeamPlayer(player, teamManager);
        }
        else if (teamPlayer.getTeam() == this){
            player.sendMessage(ChatColor.RED + "You are already on this team");
            return;
        }
        else {
            teamPlayer.getTeam().removePlayer(teamPlayer);
        }

        players.add(teamPlayer);
        team.addEntry(teamPlayer.getPlayer().getDisplayName());
        teamPlayer.setTeam(this);
        player.sendMessage(ChatColor.GOLD + "You joined team " + displayName);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        tabManager.updateTab();
    }

    public void removePlayer(TeamPlayer player) {
        if (players.contains(player)) {
            players.remove(player);
            team.removeEntry(player.getPlayer().getName());
            tabManager.updateTab();
        }
    }

}
