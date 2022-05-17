package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;

public class BoardAnimToClientPacket {
    public static final Identifier PACKET_ID = new Identifier("onewheel", "server_to_board_anim_packet");
    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    int anim = buf.readInt();
                    int anim1 = buf.readInt();
                    float yaw = buf.readFloat();
                    client.execute(() -> {
                        if (client.player.getVehicle() != null) {
                            OneWheelEntity entity = (OneWheelEntity) client.player.getVehicle();
                            entity.playAnimation = OneWheelEntity.getAnimation(anim);
                            entity.playAnimation1 = OneWheelEntity.getAnimation(anim1);
                        }
                    });
                });
    }
}
