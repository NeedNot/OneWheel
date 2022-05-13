package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class InputPacket {
    public static Identifier PACKET_ID = new Identifier("onewheel", "player_input_controls");

    public static void registerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID,
                (minecraftServer, serverPlayer, packetListener, buf, packetSender) -> {
                    boolean pressingForward = buf.readBoolean();
                    boolean pressingBack = buf.readBoolean();
                    boolean pressingLeft = buf.readBoolean();
                    boolean pressingRight = buf.readBoolean();
                    boolean pressingCtrl = buf.readBoolean();
                    minecraftServer.execute(() -> {
                        if(serverPlayer == null) {
                            return;
                        }
                        if (serverPlayer.getVehicle() instanceof OneWheelEntity) {
                            OneWheelEntity ow = (OneWheelEntity) serverPlayer.getVehicle();
                            ow.pressingForward = pressingForward;
                            ow.pressingBack = pressingBack;
                            ow.pressingLeft = pressingLeft;
                            ow.pressingRight = pressingRight;
                            ow.pressingCtrl = pressingCtrl;
                        }
                    });
                });
    }
}
