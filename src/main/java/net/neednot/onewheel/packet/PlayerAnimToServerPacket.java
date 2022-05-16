package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public class PlayerAnimToServerPacket {

    public static Identifier PACKET_ID = new Identifier("onewheel", "player_to_server_anim_packet");

    public static void registerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (minecraftServer, serverPlayer, packetListener, buf, packetSender) -> {
                    int animationBuilder = buf.readInt();
                    int id = buf.readInt();
                    minecraftServer.execute(() -> {
                        PacketByteBuf buf1 = PacketByteBufs.create();
                        buf1.writeInt(animationBuilder);
                        buf1.writeInt(id);
                        Entity entity = serverPlayer.getWorld().getEntityById(id);
                        for (ServerPlayerEntity player1 : PlayerLookup.tracking(entity)) {
                            ServerPlayNetworking.send(player1, PlayerAnimToClient.PACKET_ID, buf1);
                        }
                    });
                });
    }

}
