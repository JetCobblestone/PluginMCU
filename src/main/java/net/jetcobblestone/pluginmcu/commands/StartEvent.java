package net.jetcobblestone.pluginmcu.commands;

import net.jetcobblestone.pluginmcu.tab.TabManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StartEvent implements CommandExecutor {
    private final TabManager tabManager;
    public StartEvent(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender.isOp())) {
            sender.sendMessage(ChatColor.RED + "Only a server OP can use this command");
            return false;
        }

        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /startevent");
            return false;
        }

        //start event code
        tabManager.updateTab();
        return true;
    }


}
