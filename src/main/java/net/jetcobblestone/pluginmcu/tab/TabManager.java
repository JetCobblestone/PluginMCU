package net.jetcobblestone.pluginmcu.tab;

import com.comphenix.protocol.wrappers.*;
import com.mojang.datafixers.util.Pair;
import net.jetcobblestone.pluginmcu.packets.WrapperPlayServerPlayerInfo;
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

        for (int i = 0; i < 20; i++) {
            final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
            playerDataList.add(createFakeData("!" + playerCounter.getAndInc(), graySkin.getFirst(), graySkin.getSecond(), ""));
        }

        final List<MCUTeam> teamsList = teamManager.getTeamsList();
        for (int i = 0; i < teamsList.size(); i++) {
            if (i == 3) {
                for (int j = 0; j < 8; j++) {
                    final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
                    playerDataList.add(createFakeData("!" + playerCounter.getAndInc(), graySkin.getFirst(), graySkin.getSecond(), ""));
                }
            }

            MCUTeam mcuTeam = teamsList.get(i);
            final ChatColor teamColour = mcuTeam.getTeam().getColor();
            final Pair<String, String> skin = ColourMapper.getSkinfromColour(teamColour);

            final String teamName = playerCounter.getAndInc();
            final PlayerInfoData playerData = createFakeData("!" + teamName, skin.getFirst(), skin.getSecond(), mcuTeam.getDisplayName());
            playerDataList.add(playerData);


            final Set<String> teamEntries = mcuTeam.getTeam().getEntries();

            for (String entry : teamEntries) {
                final Player player = Bukkit.getPlayer(entry);
                if (player == null) continue;
                playerDataList.add(createFakePlayer("!" + playerCounter.getAndInc(), player.getDisplayName(), player));
            }

            for (int j = 0; j < (3 - teamEntries.size()); j++) {
                final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
                playerDataList.add(createFakeData("!" + playerCounter.getAndInc(), graySkin.getFirst(), graySkin.getSecond(), ""));
            }
        }
        for (int i = 0; i < 28; i++) {
            final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
            playerDataList.add(createFakeData("!" + playerCounter.getAndInc(), graySkin.getFirst(), graySkin.getSecond(), ""));
        }

        addPlayersPacket.setData(playerDataList);
        addPlayersPacket.sendPacket(reciever);

    }

    public PlayerInfoData createFakeData(String name, String texture, String signature, String display) {
        return createFakeData(name, new WrappedSignedProperty("textures", texture, signature), display);

    }

    public PlayerInfoData createFakeData(String name, WrappedSignedProperty property, String display) {

        final UUID uuid = UUID.randomUUID();

        final WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, name);
        gameProfile.getProperties().put("textures", property);
        return new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(display));
    }

    public PlayerInfoData createFakePlayer(String name, String display, Player player) {
        return  createFakeData(name, WrappedGameProfile.fromPlayer(player).getProperties().get("textures").iterator().next(), display);
    }
}
