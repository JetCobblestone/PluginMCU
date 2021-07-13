package net.jetcobblestone.pluginmcu.tab;

import com.comphenix.protocol.wrappers.*;
import com.mojang.datafixers.util.Pair;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerPlayerInfo;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerScoreboardTeam;
import net.jetcobblestone.pluginmcu.team.ColourMapper;
import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TabManager {

    private TeamManager teamManager;
    private final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    private final List<EntityPlayer> fakeDisplayedPlayers = new ArrayList<>();

    public void init(TeamManager teamManager) {
        this.teamManager = teamManager;
    }


    public void updateTab(Player reciever) {

        final List<PlayerInfoData> playerDataList = new ArrayList<>();
        final WrapperPlayServerPlayerInfo addPlayersPacket = new WrapperPlayServerPlayerInfo();
        addPlayersPacket.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        final OrderCounter playerCounter = new OrderCounter(2);
        final OrderCounter teamCounter = new OrderCounter(2);

        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            final ChatColor teamColour = mcuTeam.getTeam().getColor();
            final com.mojang.datafixers.util.Pair<String, String> skin = ColourMapper.getSkinfromColour(teamColour);

            final String teamName = playerCounter.getAndInc();
            final PlayerInfoData playerData = createFakeData("!" + teamName, skin.getFirst(), skin.getSecond(), mcuTeam.getDisplayName());
            playerDataList.add(playerData);

            final WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam();
            teamPacket.setMode(0);
            teamPacket.setName(teamCounter.getAndInc());
            teamPacket.setPlayers(Collections.singletonList(teamName));
            teamPacket.sendPacket(reciever);

            final Set<String> teamEntries = mcuTeam.getTeam().getEntries();

            teamCounter.inc(1);
            for (int i = 0; i < (3 - teamEntries.size()); i++) {
                final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
                final String memberName = playerCounter.getAndInc();
                playerDataList.add(createFakeData("!" + memberName, graySkin.getFirst(), graySkin.getSecond(), ""));

                final WrapperPlayServerScoreboardTeam memberTeamPacket = new WrapperPlayServerScoreboardTeam();
                memberTeamPacket.setMode(0);
                memberTeamPacket.setName(teamCounter.getAndInc());
                memberTeamPacket.setPlayers(Collections.singletonList(memberName));
                memberTeamPacket.sendPacket(reciever);
            }
        }

        addPlayersPacket.setData(playerDataList);
        addPlayersPacket.sendPacket(reciever);

    }

    public PlayerInfoData createFakeData(String name, String texture, String signature, String display) {
        final UUID uuid = UUID.randomUUID();

        final WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, name);
        gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", texture, signature));
        return new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(display));
    }
}
