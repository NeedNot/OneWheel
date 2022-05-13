package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;

import java.util.UUID;

public class AssignPlayerPacket {
    public static Identifier PACKET_ID = new Identifier("onewheel", "player_assign_packet");

    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    UUID uuid = buf.readUuid();
                    int id = buf.readInt();
                    PlayerEntity player = handler.getWorld().getPlayerByUuid(uuid);
                    OneWheelPlayerEntity oneWheelPlayer = (OneWheelPlayerEntity) handler.getWorld().getEntityById(id);
                    client.execute(() -> {
                        oneWheelPlayer.setAssignedPlayer(player);
                    });
                });
    }
}
