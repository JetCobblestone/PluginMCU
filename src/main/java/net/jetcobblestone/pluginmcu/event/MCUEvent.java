package net.jetcobblestone.pluginmcu.event;

import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.jetcobblestone.pluginmcu.team.TeamPlayer;
import net.jetcobblestone.pluginmcu.util.ActiveCooldown;
import net.jetcobblestone.pluginmcu.util.Sidebar;
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

        Sidebar.setSize(0);
        Sidebar.setSize(7);
        Sidebar.setDisplayName(ChatColor.GREEN + "" + ChatColor.UNDERLINE + "Event MCU");
        Sidebar.setScore(3, ChatColor.GREEN + "Status: " + ChatColor.YELLOW + "Waiting");
        Sidebar.setScore(1, ChatColor.AQUA + "Coded by: " + ChatColor.RED + "JetCobblestone");
        Sidebar.showSideBar(true);


        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            for (String playerName : mcuTeam.getTeam().getEntries()) {
                final Player player = Objects.requireNonNull(Bukkit.getPlayer(playerName));
                final TeamPlayer teamPlayer = teamManager.getTeamPlayer(player);
                participants.add(teamPlayer);
                player.sendTitle(ChatColor.GOLD + "Welcome to the MCU!", "", 10, 60, 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.sendTitle(null, ChatColor.GREEN + "You are on team " + teamPlayer.getTeam().getDisplayName(), 0, 40, 20);
                }, 30);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }

        final AtomicInteger count = new AtomicInteger(60);
        ActiveCooldown c = new ActiveCooldown((60*20), 20, integer -> {
            Sidebar.setScore(6, ChatColor.YELLOW + "Event starting in " + count.getAndDecrement());
        }, () -> {
            Bukkit.getConsoleSender().sendMessage("Countdown ended");
        }, scheduleManager);

    }

}
