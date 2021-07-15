package net.jetcobblestone.pluginmcu.team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerNamedEntitySpawn;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerPlayerInfo;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class MCUTeam {
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
        tabManager.set((teamNumber*4) + 1, TabManager.getFromColour(displayName, (teamNumber*4) + 1, team.getColor()));
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
        team.addEntry(teamPlayer.getPlayer().getName());
        player.setDisplayName(team.getColor() + player.getDisplayName() + ChatColor.RESET);
        teamPlayer.setTeam(this);


        final int position = (teamNumber * 4) + 1 + players.size();
        tabManager.set(player.getDisplayName(), position, player);

        player.sendMessage(ChatColor.GOLD + "You joined team " + displayName);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    public void removePlayer(TeamPlayer teamPlayer) {
        if (players.contains(teamPlayer)) {
            int position = players.indexOf(teamPlayer);
            int shift = (teamNumber*4) +1;

            for (int i = position+1; i <= players.size(); i++) {
                if (i == players.size()) {
                    tabManager.clear(shift+i);
                }
                else {
                    Player next = players.get(i).getPlayer();
                    tabManager.set(next.getDisplayName(), shift+i, next);
                }
            }

            players.remove(teamPlayer);
            team.removeEntry(teamPlayer.getPlayer().getName());
            teamPlayer.setTeam(null);
            final Player player = teamPlayer.getPlayer();
            player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
        }
    }


}
