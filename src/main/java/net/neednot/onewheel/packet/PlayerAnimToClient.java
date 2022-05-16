package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;

public class PlayerAnimToClient {
    public static final Identifier PACKET_ID = new Identifier("onewheel", "server_to_player_anim_packet");
    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    int anim = buf.readInt();
                    int id = buf.readInt();
                    client.execute(() -> {
                        OneWheelPlayerEntity entity = (OneWheelPlayerEntity) client.world.getEntityById(id);
                        entity.playAnimation = OneWheelPlayerEntity.getAnimation(anim);
                    });
                });
    }
}
