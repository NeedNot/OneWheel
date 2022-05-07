package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class FallPacket {

    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(OneWheel.PACKET_ID ,
                (client, handler, buf, responseSender) -> {
                boolean bool = buf.readBoolean();
                client.execute(() -> {
                    if (client.player.hasVehicle() && bool) {
                        if (client.player.getVehicle() instanceof OneWheelEntity) {
                            System.out.println("packet sent crash");
                            OneWheelEntity ow = (OneWheelEntity) client.player.getVehicle();
                            ow.needSpeed = 0.1f;
                        }
                    }
                });
            });
    }
}
