package net.neednot.onewheel.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class BatteryPacket {
    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(OneWheel.BATTERY ,
                (client, handler, buf, responseSender) -> {
                    System.out.println("I GOT THE BATTERY");
                    float level = buf.readFloat();
                    client.execute(() -> {
                        System.out.println("packet sent battery");
                        OneWheelEntity ow = (OneWheelEntity) MinecraftClient.getInstance().player.getVehicle();
                        ow.setBattery(level);
                        System.out.println(level);
                    });
                });
    }
}
