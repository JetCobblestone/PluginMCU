package net.jetcobblestone.pluginmcu.scoreboard;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Getter;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerScoreboardDisplayObjective;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerScoreboardObjective;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class PacketBoard {

    @Getter private static Map<UUID, PacketBoard> playerMap = new HashMap<>();

    private String displayName = "un-named scoreboard";
    private DisplaySlot displaySlot = DisplaySlot.SIDEBAR;

    private final String[] scores = new String[16];
    private final List<Integer> blanks = new ArrayList<>();
    private final Set<UUID> uuids = new HashSet<>();
    private int size = 0;

    public enum DisplaySlot {
        PLAYER_LIST(0),
        SIDEBAR(1),
        BELOW_NAME(2);

        private final int position;
        DisplaySlot(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public void setDisplaySlot(DisplaySlot displaySlot) {
        this.displaySlot = displaySlot;

        final WrapperPlayServerScoreboardDisplayObjective displayObjectivePacket = new WrapperPlayServerScoreboardDisplayObjective();
        displayObjectivePacket.setPosition(displaySlot.getPosition());
        displayObjectivePacket.setScoreName("display");
        displayObjectivePacket.sendPacketUUID(uuids);
    }

    public void addPlayer(Player player) {

        final PacketBoard packetBoard = playerMap.get(player.getUniqueId());
        if (packetBoard != null) {
            packetBoard.removePlayer(player);
        }

        final UUID uuid = player.getUniqueId();
        uuids.add(uuid);
        playerMap.put(uuid, this);

        initPlayer(player);
    }

    protected void initPlayer(Player player) {
        final WrapperPlayServerScoreboardObjective objectivePacket = new WrapperPlayServerScoreboardObjective();
        objectivePacket.setName("display");
        objectivePacket.setDisplayName(WrappedChatComponent.fromText(displayName));
        objectivePacket.setMode(0);
        objectivePacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
        objectivePacket.sendPacket(player);

        final WrapperPlayServerScoreboardDisplayObjective displayObjectivePacket = new WrapperPlayServerScoreboardDisplayObjective();
        displayObjectivePacket.setPosition(displaySlot.getPosition());
        displayObjectivePacket.setScoreName("display");
        displayObjectivePacket.sendPacket(player);

        final List<UUID> receiver = Arrays.asList(player.getUniqueId());

        for (int i = 1; i <= size; i++) {
            setScore(i, scores[i], receiver);
        }
    }

    public void removePlayer(Player player) {
        final UUID uuid = player.getUniqueId();
        uuids.remove(uuid);
        playerMap.put(uuid, null);

        final List<UUID> receiver = Arrays.asList(player.getUniqueId());

        for (int i = 1; i <= size; i++) {
            removeScore(scores[i], receiver);
        }

        final WrapperPlayServerScoreboardObjective objectivePacket = new WrapperPlayServerScoreboardObjective();
        objectivePacket.setName("display");
        objectivePacket.setMode(1);
        objectivePacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
        objectivePacket.sendPacket(player);

        final WrapperPlayServerScoreboardDisplayObjective displayObjectivePacket = new WrapperPlayServerScoreboardDisplayObjective();
        displayObjectivePacket.setPosition(displaySlot.getPosition());
        displayObjectivePacket.setScoreName("");
        displayObjectivePacket.sendPacket(player);
    }


    private void removeScore(String score, Collection<UUID> uuidCollection) {

        final WrapperPlayServerScoreboardScore removeScorePacket = new WrapperPlayServerScoreboardScore();
        removeScorePacket.setObjectiveName("display");
        removeScorePacket.setScoreboardAction(EnumWrappers.ScoreboardAction.REMOVE);
        removeScorePacket.setScoreName(score);
        removeScorePacket.sendPacketUUID(uuidCollection);
    }


    public void setScore(int position, String value) {
        setScore(position, value, uuids);
    }

    private void setScore(int position, String value, Collection<UUID> uuidCollection) {
        if (position > size || position <= 0) {
            Bukkit.getLogger().severe("Attempted to set a score outside the range of the sidebar (" + position + ")");
            Thread.dumpStack();
            return;
        }

        if (value.trim().isEmpty()) {
            blanks.add(position);
            calculateBlanks();
            return;
        }

        final String score = scores[position];
        if(score != null) {
            removeScore(score, uuidCollection);
        }

        scores[position] = value;
        blanks.remove(Integer.valueOf(position));

        final WrapperPlayServerScoreboardScore addScorePacket = new WrapperPlayServerScoreboardScore();
        addScorePacket.setObjectiveName("display");
        addScorePacket.setScoreboardAction(EnumWrappers.ScoreboardAction.CHANGE);
        addScorePacket.setScoreName(value);
        addScorePacket.setValue(position);
        addScorePacket.sendPacketUUID(uuidCollection);
    }


    public void setDisplayName(String name) {
        displayName = name + ChatColor.RESET;
        final WrapperPlayServerScoreboardObjective objectivePacket = new WrapperPlayServerScoreboardObjective();
        objectivePacket.setName("display");
        objectivePacket.setDisplayName(WrappedChatComponent.fromText(displayName));
        objectivePacket.setMode(2);
        objectivePacket.setHealthDisplay(WrapperPlayServerScoreboardObjective.HealthDisplay.INTEGER);
        objectivePacket.sendPacketUUID(uuids);
    }


    public void setSize(int size) {
        if (size > 16 || size < 0) {
            Bukkit.getLogger().severe("Attempted to set sidebar size to a value greater than 16 or less than 1");
            Thread.dumpStack();
            return;
        }

        if (this.size == size) {
            return;
        }

        final int oldSize = this.size;
        this.size = size;

        if (size < oldSize) {
            for (int i = (size +1); i <= oldSize; i++) {

                removeScore(scores[i], uuids);

                scores[i] = null;
                blanks.remove(Integer.valueOf(i));
            }
        }
        else {
            for (int i = (oldSize + 1); i <= size; i++) {
                blanks.add(i);
            }
            calculateBlanks();
        }
    }

    private void calculateBlanks() {
        String blank = " ";
        for (int index : blanks) {

            final String score = scores[index];
            if(score != null) {
                removeScore(score, uuids);
            }

            scores[index] = blank;

            final WrapperPlayServerScoreboardScore addScorePacket = new WrapperPlayServerScoreboardScore();
            addScorePacket.setObjectiveName("display");
            addScorePacket.setScoreboardAction(EnumWrappers.ScoreboardAction.CHANGE);
            addScorePacket.setScoreName(blank);
            addScorePacket.setValue(index);
            addScorePacket.sendPacketUUID(uuids);

            blank = blank + " ";
        }
    }

    public PacketBoard clone() {
        final PacketBoard packetBoard = new PacketBoard();
        packetBoard.setSize(size);
        packetBoard.setDisplayName(displayName);
        for (int i = 1; i <= size; i++) {
            packetBoard.setScore(i, scores[i]);
        }
        return packetBoard;
    }

}
