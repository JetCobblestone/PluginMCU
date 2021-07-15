package net.jetcobblestone.pluginmcu.util;

import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Sidebar {

    @SuppressWarnings("ConstantConditions")
    private static final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    private static final Objective objective = scoreboard.registerNewObjective("sidebar", "dummy", "display");
    private static final String[] scores = new String[16];
    private static final List<Integer> blanks = new ArrayList<>();
    private static int size = 0;

    public static void showSideBar(boolean bool) {

        if (bool) objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        else scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    }

    public static void setDisplayName(String name) {
        objective.setDisplayName(name);
    }

    public static void setSize(int size) {
        if (size > 16 || size < 0) {
            Bukkit.getLogger().severe("Attempted to set sidebar size to a value greater than 16 or less than 1");
            Thread.dumpStack();
            return;
        }
        if (size < Sidebar.size) {
            for (int i = (size +1); i <= Sidebar.size; i++) {

                Bukkit.getLogger().info(i + "");
                Bukkit.getLogger().info(scores[i]);

                scoreboard.resetScores(scores[i]);
                scores[i] = null;
                blanks.remove(Integer.valueOf(i));
            }
        }
        else {
            for (int i = (Sidebar.size + 1); i <= size; i++) {
                blanks.add(i);
            }
            calculateBlanks();
        }
        Sidebar.size = size;
    }

    private static void calculateBlanks() {
        String blank = " ";
        for (int index : blanks) {
            if (scores[index] != null) {
                scoreboard.resetScores(scores[index]);
            }
            scores[index] = blank;
            objective.getScore(blank).setScore(index);
            blank = blank + " ";
        }
    }

    public static void setScore(int position, String value) {
        if (position > size || size < 0) {
            Bukkit.getLogger().severe("Attempted to set a score outside the range of the sidebar");
            Thread.dumpStack();
            return;
        }
        if (value.trim().isEmpty()) {
            calculateBlanks();
            return;
        }
        scoreboard.resetScores(scores[position]);
        scores[position] = value;
        blanks.remove(Integer.valueOf(position));
        objective.getScore(value).setScore(position);
    }

    public static void setBlank(int position) {
        if (position > size || size < 0) {
            blanks.add(position);
            calculateBlanks();
        }
    }
}
