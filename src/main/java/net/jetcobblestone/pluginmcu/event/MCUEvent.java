package net.jetcobblestone.pluginmcu.event;

import net.jetcobblestone.pluginmcu.scoreboard.PacketBoard;
import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.team.TeamPlayer;
import net.jetcobblestone.pluginmcu.util.ActiveCooldown;
import net.jetcobblestone.pluginmcu.util.annotationscheduling.ScheduleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class MCUEvent {

    private final List<TeamPlayer> participants = new ArrayList<>();
    private final TeamManager teamManager;

    public MCUEvent(JavaPlugin plugin, TeamManager teamManager, ScheduleManager scheduleManager) {
        this.teamManager = teamManager;

        final PacketBoard packetBoard = new PacketBoard();

        packetBoard.setSize(9);
        packetBoard.setDisplayName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Minecraft Cinematic Universe");
        packetBoard.setScore(8, ChatColor.YELLOW + "Event is starting soon...");
        packetBoard.setScore(1, ChatColor.AQUA + "Coded by: " + ChatColor.RED + "JetCobblestone");



        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            for (String playerName : mcuTeam.getTeam().getEntries()) {
                final Player player = Objects.requireNonNull(Bukkit.getPlayer(playerName));
                final TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);
                final PacketBoard copy = packetBoard.clone();
                copy.addPlayer(teamPlayer.getPlayer());

                copy.setScore(5, ChatColor.YELLOW + "Your team: " + mcuTeam.getDisplayName());
                copy.setScore(4, ChatColor.YELLOW + "Team score: " + ChatColor.RED + "0");
                copy.setScore(3, ChatColor.YELLOW + "Individual score: " + ChatColor.RED + "0");

                participants.add(teamPlayer);
                player.sendTitle(ChatColor.GOLD + "Welcome to the MCU!", "", 10, 60, 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendTitle(null, ChatColor.GREEN + "You are on team " + teamPlayer.getTeam().getDisplayName(), 0, 40, 20);
                }, 30);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            }
        }

        final AtomicInteger count = new AtomicInteger(60);
        new ActiveCooldown((60*20), 20, integer -> {
            final int timeLeft = count.getAndDecrement();
            for (TeamPlayer player : participants) {
                PacketBoard.getPlayerMap().get(player.getPlayer().getUniqueId()).setScore(7, "Time: " + timeLeft);
            }
        }, () -> {
            Bukkit.getConsoleSender().sendMessage("Countdown ended");
        }, scheduleManager);

    }

}
