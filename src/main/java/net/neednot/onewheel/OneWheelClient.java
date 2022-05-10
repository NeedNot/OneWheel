package net.neednot.onewheel;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.board.OneWheelRender;
import net.neednot.onewheel.entity.player.OneWheelPlayerRender;
import net.neednot.onewheel.packet.BatteryPacket;
import net.neednot.onewheel.packet.FallPacket;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class OneWheelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
            Entity entity;
            int scaledwidth;
            int scaledheight;
            if ((entity = MinecraftClient.getInstance().player.getVehicle()) != null) {
                if (entity instanceof OneWheelEntity) {
                    OneWheelEntity ow = (OneWheelEntity) entity;
                    scaledwidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
                    scaledheight = MinecraftClient.getInstance().getWindow().getScaledHeight();
                    //battery png
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/noprogress.png"));
                    DrawableHelper.drawTexture(matrixStack, scaledwidth/2+10, scaledheight-40, 0, 0, 0, 81,10,81,10);

                    float ratio = 0;
                    if (ow.battery != 0) {
                        ratio = (float) (Math.abs(ow.battery)/32186.88f);
                    }
                    int width = (int) (81*ratio);
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/100.png"));
                    DrawableHelper.drawTexture(matrixStack, scaledwidth/2+10, scaledheight-40, 0, 0, 0, width,10, 81,10);

                    //battery mph string
                    String percent = String.format("%.0f", (ow.battery/32186.88f)*100) +"%";
                    int x = 0;
                    if (percent.length() == 2) {
                        x = 45;
                    }
                    if (percent.length() == 3) {
                        x = 42;
                    }
                    if (percent.length() == 4) {
                        x = 39;
                    }
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, percent, scaledwidth/2+x, scaledheight-39, 0x0f81c9);

                    //speed mph png
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/noprogress.png"));
                    DrawableHelper.drawTexture(matrixStack, scaledwidth/2+10, scaledheight-50, 0, 0, 0, 81,10,81,10);

                    ratio = 0;
                    if (ow.f != 0) {
                        ratio = (float) (Math.abs(ow.f)/1.1111);
                    }
                    width = (int) (80*ratio);
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/progress.png"));
                    DrawableHelper.drawTexture(matrixStack, scaledwidth/2+10, scaledheight-50, 0, 0, 0, width,10, 81,10);

                    //speed mph string
                    String speed = String.format("%.2f", Math.abs(ow.f*27)) +" MPH";
                    x = 21-(9/2);
                    if (speed.length() == 8) {
                        x = 21-(3/2);
                    }
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, speed, scaledwidth/2+10+x, scaledheight-49, 0x0f81c9);
                }
            }
        }));
        FallPacket.registerPacket();
        BatteryPacket.registerPacket();
        EntityRendererRegistry.register(OneWheel.OWPE, OneWheelPlayerRender::new);
        EntityRendererRegistry.register(OneWheel.OW, OneWheelRender::new);
    }
}
