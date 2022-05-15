package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;

import java.util.UUID;

public class SpawnFakePlayerPacket {
    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(OneWheel.FAKE_PLAYER_PACKET,
                (client, handler, buf, responseSender) -> {
                    UUID uuid = UUID.fromString(buf.readString());
                    int id = buf.readInt();
                    client.execute(() -> {
                        PlayerEntity player = client.world.getPlayerByUuid(uuid);
                        Entity entity = player.world.getEntityById(id);
                        if (entity instanceof OneWheelPlayerEntity) {
                            OneWheelPlayerEntity owpe = (OneWheelPlayerEntity) entity;
                            owpe.assignedPlayer = player;
                        }
                    });
                });
    }
}
