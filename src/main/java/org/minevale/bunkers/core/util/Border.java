package org.minevale.bunkers.core.util;

import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_8_R3.WorldBorder;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.minevale.bunkers.core.BunkersCore;
import org.minevale.bunkers.core.player.bunker.PlayerBunker;

import java.util.ArrayList;
import java.util.List;

public enum Border {

    RED,
    BLUE,
    GREEN;

    public static List<Packet> getPackets(Border color, double x, double z, double radius) {
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.setCenter(x, z);
        worldBorder.setSize(radius);
        worldBorder.setWarningDistance(0);

        List<Packet> packets = new ArrayList<>();
        packets.add(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
        packets.add(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
        packets.add(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));


        switch (color) {
            case RED:
                worldBorder.transitionSizeBetween(radius, radius - 1.0D, 20000000L);
                packets.add(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
                break;
            case GREEN:
                worldBorder.transitionSizeBetween(radius - 0.2D, radius - 0.1D + 0.1D, 20000000L);
                packets.add(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
        }
        return packets;
    }

    public static void handleBorderUpdate(Player player, PlayerBunker bunker) {
        if (player == null) return;

        new BukkitRunnable() {


            public void run() {
                if (bunker == null) {
                    Border.getPackets(Border.BLUE, 0.0D, 0.0D, 1.4999992E7D)
                            .forEach(((CraftPlayer) player).getHandle().playerConnection::sendPacket);
                    return;
                }

                if (bunker.getBounds().contains(player.getLocation())) {
                    Location center = bunker.getBounds().getCenter();
                    Border.getPackets(Border.BLUE, center.getX(), center.getZ(), bunker.getBounds().getSizeX() * bunker.getBounds().getSizeZ())
                            .forEach(((CraftPlayer) player).getHandle().playerConnection::sendPacket);
                    System.out.println("Completed");
                }
            }

        }.runTaskLater(BunkersCore.getInstance(), 10L);
    }
}