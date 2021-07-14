package net.jetcobblestone.pluginmcu.commands;

import net.jetcobblestone.pluginmcu.event.MCUEventManager;
import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartEvent implements CommandExecutor {
    private final MCUEventManager eventManager;

    public StartEvent(MCUEventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.RED + "Only a server OP can use this command");
            return false;
        }

        if (eventManager.eventActive()) {
            sender.sendMessage(ChatColor.RED + "An event is already in progress");
            return false;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /startevent");
            return false;
        }

        //start event code
        eventManager.startEvent();
        return true;
    }


}
