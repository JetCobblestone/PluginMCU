package net.jetcobblestone.pluginmcu.commands.tabs;

import net.jetcobblestone.pluginmcu.team.ColourMapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinTeamTab implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        final List<String> colourNames = Arrays.asList(ColourMapper.getColourNames());
        if (args.length == 0) {
            return colourNames;
        }

        final List<String> result = new ArrayList<>();
        if (args.length > 1) {
            return result;
        }

        for (String colour : colourNames) {
            String input = args[0];
            if (input.length() > colour.length()) continue;
            if (colour.startsWith(input)) {
                result.add(colour);
            }
        }
        return result;
    }
}
