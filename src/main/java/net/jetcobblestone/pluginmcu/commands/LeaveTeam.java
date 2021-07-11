package net.jetcobblestone.pluginmcu.commands;

import net.jetcobblestone.pluginmcu.event.EventManager;
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

public class LeaveTeam implements CommandExecutor {
    private final TeamManager teamManager;
    private final EventManager eventManager;

    public LeaveTeam(TeamManager teamManager, EventManager eventManager) {
        this.teamManager = teamManager;
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player");
            return false;
        }

        final Player player = (Player) sender;

        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Usage: /leaveteam");
            return false;
        }

        if (eventManager.eventActive()) {
            player.sendMessage(ChatColor.RED + "You cannot leave a team whilst an event is in progress");
            return false;
        }

        final TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);
        final MCUTeam mcuTeam = teamPlayer.getTeam();

        if (mcuTeam == null) {
            player.sendMessage(ChatColor.RED + "You are not in a team");
            return false;
        }

        mcuTeam.removePlayer(teamPlayer);
        player.sendMessage(ChatColor.GOLD + "You left team " + mcuTeam.getDisplayName());
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        return true;
    }
}
