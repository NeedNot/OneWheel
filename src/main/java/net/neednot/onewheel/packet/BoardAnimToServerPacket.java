package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BoardAnimToServerPacket {
    public static Identifier PACKET_ID = new Identifier("onewheel", "board_to_server_anim_packet");

    public static void registerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (minecraftServer, serverPlayer, packetListener, buf, packetSender) -> {
                    int animationBuilder = buf.readInt();
                    int animationBuilder1 = buf.readInt();
                    int id = buf.readInt();
                    float speed = buf.readFloat();
                    minecraftServer.execute(() -> {
                        PacketByteBuf buf1 = PacketByteBufs.create();
                        buf1.writeInt(animationBuilder);
                        buf1.writeInt(animationBuilder1);
                        buf1.writeInt(id);
                        buf1.writeFloat(speed);
                        Entity entity = serverPlayer.getWorld().getEntityById(id);
                        if (entity != null) {
                            for (ServerPlayerEntity player1 : PlayerLookup.tracking(entity)) {
                                ServerPlayNetworking.send(player1 , BoardAnimToClientPacket.PACKET_ID , buf1);
                            }
                        }
                    });
                });
    }
}
