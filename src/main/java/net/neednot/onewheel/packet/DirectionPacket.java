package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class DirectionPacket {
    public static Identifier PACKET_ID = new Identifier("onewheel", "player_to_server_direction_packet");

    public static void registerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (minecraftServer, serverPlayer, packetListener, buf, packetSender) -> {
                    float prevYaw = buf.readFloat();
                    float yaw = buf.readFloat();
                    System.out.println(yaw-prevYaw);
                    minecraftServer.execute(() -> {
                        if (serverPlayer.getVehicle() instanceof OneWheelEntity) {
                            OneWheelEntity ow = (OneWheelEntity) serverPlayer.getVehicle();
                            ow.spin = (float) Math.toDegrees(yaw-prevYaw);
                        }
                    });
                });
    }
}
