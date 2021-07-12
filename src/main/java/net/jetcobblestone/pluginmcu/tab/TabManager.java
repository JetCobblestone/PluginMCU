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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TabManager {

    private TeamManager teamManager;
    private final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
    private final List<EntityPlayer> fakeDisplayedPlayers = new ArrayList<>();

    public void init(TeamManager teamManager) {
        this.teamManager = teamManager;
        updateTab();
    }


    public void updateTab() {
        Bukkit.getLogger().info("Function ran");
        final WrapperPlayServerPlayerInfo packetWrapper = new WrapperPlayServerPlayerInfo();
        packetWrapper.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        List<PlayerInfoData> playerDataList = new ArrayList<>();

        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            final ChatColor teamColour = mcuTeam.getTeam().getColor();
            final com.mojang.datafixers.util.Pair<String, String> skin = ColourMapper.getSkinfromColour(teamColour);
            final PlayerInfoData playerData = createFakeData(mcuTeam.getDisplayName(), skin.getFirst(), skin.getSecond());
            playerDataList.add(playerData);

            for (int i = 0; i < (3 - mcuTeam.getTeam().getEntries().size()); i++) {
                final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
                playerDataList.add(createFakeData("", graySkin.getFirst(), graySkin.getSecond()));
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            packetWrapper.sendPacket(player);
        }
    }

    public PlayerInfoData createFakeData(String name, String texture, String signature) {

        final UUID uuid = UUID.randomUUID();

        final WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, uuid.toString().substring(0,16));
        gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", texture, signature));
        return new PlayerInfoData(gameProfile, 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(name));
    }
}
