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
    private final int teamNumber;

    public MCUTeam(String colourName, ChatColor colour, int teamNumber, TeamManager teamManager, TabManager tabManager) {
        this.colourName = colourName;
        this.teamNumber = teamNumber;
        this.teamManager = teamManager;
        this.tabManager = tabManager;
        this.team = teamManager.getTeamBoard().registerNewTeam(colourName);

        team.setColor(colour);
        setDisplayName(colourName);
    }

    public void setDisplayName(String name) {
        displayName = team.getColor() + "" + ChatColor.BOLD + name;
        tabManager.set(teamNumber*4, TabManager.getFromColour(displayName, teamNumber*4, team.getColor()));
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

        final int position = (teamNumber * 4) + players.size();
        tabManager.set(position,TabManager.createFakePlayer(player.getDisplayName(), position, player));

        player.sendMessage(ChatColor.GOLD + "You joined team " + displayName);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removePlayer(TeamPlayer teamPlayer) {
        if (players.contains(teamPlayer)) {
            tabManager.clear((teamNumber * 4) + players.size());
            players.remove(teamPlayer);
            team.removeEntry(teamPlayer.getPlayer().getName());
            teamPlayer.setTeam(null);
        }
    }

}
