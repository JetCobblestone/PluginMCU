package net.jetcobblestone.pluginmcu.commands;

import net.jetcobblestone.pluginmcu.event.MCUEventManager;
import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.team.TeamPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RenameTeam implements CommandExecutor {

    //TODO - only can change team name before event start

    private final TeamManager teamManager;
    private final MCUEventManager eventManager;

    public RenameTeam(TeamManager teamManager, MCUEventManager eventManager) {
        this.teamManager = teamManager;
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only a player in a team can use this command");
            return false;
        }
        final Player player = (Player) sender;

        if (eventManager.eventActive()) {
            player.sendMessage(ChatColor.RED + "You cannot rename a team whilst an event is in progress");
            return false;
        }

        final TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);

        if (teamPlayer == null) {
            player.sendMessage(ChatColor.RED + "You must be in a team to use this command");
            return false;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /renameteam [New Name]");
            return false;
        }

        final MCUTeam team = teamPlayer.getTeam();

        team.setDisplayName(args[0]);
        player.sendMessage(ChatColor.GRAY + "Team " + team.getColourName() + ChatColor.GRAY + " renamed to: " + team.getDisplayName());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        return true;
    }
}
