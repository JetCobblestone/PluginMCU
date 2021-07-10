package net.jetcobblestone.pluginmcu.tab;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.jetcobblestone.pluginmcu.OrderCounter;
import net.jetcobblestone.pluginmcu.team.ColourMapper;
import net.jetcobblestone.pluginmcu.team.MCUTeam;
import net.jetcobblestone.pluginmcu.team.TeamManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabManager {

    private TeamManager teamManager;
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    private final List<EntityPlayer> fakeDisplayedPlayers = new ArrayList<>();

    public void init(TeamManager teamManager) {
        this.teamManager = teamManager;
        updateTab();
    }

    public void resetTab() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            for (EntityPlayer entityPlayer : fakeDisplayedPlayers) {
                final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
                connection.sendPacket(packet);
            }
            for (Player other : Bukkit.getOnlinePlayers()) {
                final PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) other).getHandle());
                connection.sendPacket(packet);
            }
        }

    }

    public void displayPlayer(EntityPlayer entityPlayer, String teamName) {
        final Team team = scoreboard.registerNewTeam(teamName);
        team.addEntry(entityPlayer.getName());

        fakeDisplayedPlayers.add(entityPlayer);

        final DataWatcher dataWatcher = entityPlayer.getDataWatcher();

        dataWatcher.set(new DataWatcherObject<>(17, DataWatcherRegistry.a), (byte) 255);

        final PacketPlayOutPlayerInfo addPlayerPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        final PacketPlayOutEntityMetadata metaDataPacket = new PacketPlayOutEntityMetadata(entityPlayer.getId(), dataWatcher ,true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            final EntityPlayer target = ((CraftPlayer) player).getHandle();
            target.playerConnection.sendPacket(addPlayerPacket);
            target.playerConnection.sendPacket(metaDataPacket);
        }
    }

    public EntityPlayer getFakeCopy(Player player) {
        final GameProfile profile = ((CraftPlayer) player).getProfile();
        final Property property = profile.getProperties().get("textures").iterator().next();
        return createFakePlayer(player.getName(), property.getValue(), property.getSignature());
    }

    public void updateTab() {
        resetTab();
        for (Team team : scoreboard.getTeams()) {
            team.unregister();
        }

        final OrderCounter counter = new OrderCounter(2);

        for (MCUTeam mcuTeam : teamManager.getTeamsList()) {
            final ChatColor teamColour = mcuTeam.getTeam().getColor();
            final Pair<String, String> skin = ColourMapper.getSkinfromColour(teamColour);
            final EntityPlayer entityPlayer = createFakePlayer(mcuTeam.getDisplayName(), skin.getFirst(), skin.getSecond());
            displayPlayer(entityPlayer, counter.nextInt());

            int dif = 3 - mcuTeam.getTeam().getEntries().size();
            for (String name : mcuTeam.getTeam().getEntries() ){
                final Player player = Bukkit.getPlayer(name);
                if (player == null) continue;
                displayPlayer(getFakeCopy(player), counter.nextInt());
            }
            for (int i = 0; i < dif; i++) {
                final Pair<String, String> graySkin = ColourMapper.getSkinfromColour(ChatColor.GRAY);
                displayPlayer(createFakePlayer("", graySkin.getFirst(), graySkin.getSecond()), counter.nextInt());
            }
        }
    }

    public EntityPlayer createFakePlayer(String name, String texture, String signature) {

        final WorldServer worldServer = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        final UUID uuid = UUID.randomUUID();
        final GameProfile profile = new GameProfile(uuid, uuid.toString().substring(0,16));
        profile.getProperties().put("textures", new Property("textures", texture, signature));
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                worldServer,
                profile,
                new PlayerInteractManager(worldServer));

        entityPlayer.listName = new ChatMessage(name);
        return entityPlayer;
    }
}
