package net.jetcobblestone.pluginmcu.commands;

import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JoinTeam implements CommandExecutor {
    private final TeamManager teamManager;

    public JoinTeam(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player");
            return false;
        }

        final Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /jointeam [Team Colour]");
            return false;
        }

        final MCUTeam team = teamManager.findTeam(args[0]);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "That team could not be found, the available teams are:");
            for (MCUTeam teamIterate : teamManager.getTeamsList()) {
                final String colourName = teamIterate.getColourName();
                final String displayName = teamIterate.getDisplayName();
                if (ChatColor.stripColor(displayName).equals(colourName)) {
                    player.sendMessage(displayName);
                }
                else {
                    player.sendMessage(displayName + ChatColor.RESET + "" + teamIterate.getTeam().getColor() + " (" + colourName + ")");
                }
            }
            return false;
        }
        team.addPlayer(player, teamManager);
        return true;
    }
}
