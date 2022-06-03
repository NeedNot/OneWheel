package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class NoseDivePosPacket {
    public static Identifier PACKET_ID = new Identifier("onewheel", "nose_dive_pos_packet");
    public static void registerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (minecraftServer, serverPlayer, packetListener, buf, packetSender) -> {
                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();
                    Vec3d vec3d = new Vec3d(x, y, z);
                    minecraftServer.execute(() -> {
                        if(serverPlayer == null) {
                            return;
                        }
                        serverPlayer.setPosition(vec3d);
                    });
                });
    }
}
