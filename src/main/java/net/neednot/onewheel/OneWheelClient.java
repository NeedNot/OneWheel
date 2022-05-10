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
import net.neednot.onewheel.packet.FallPacket;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class OneWheelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
            Entity entity;
            if ((entity = MinecraftClient.getInstance().player.getVehicle()) != null) {
                if (entity instanceof OneWheelEntity) {
                    OneWheelEntity ow = (OneWheelEntity) entity;

                    //speed mph spite
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/noprogress.png"));
                    DrawableHelper.drawTexture(matrixStack, 10, 10, 0, 0, 0, 128,16,128,16);

                    float ratio = 0;
                    if (ow.f != 0) {
                       ratio = (float) (Math.abs(ow.f)/1.1111);
                    }
                    int width = (int) (128*ratio);
                    RenderSystem.setShaderTexture(0, new Identifier("onewheel", "textures/ui/progress.png"));
                    DrawableHelper.drawTexture(matrixStack, 10, 10, 0, 0, 0, width,16, 128,16);

                    //speed mph string
                    String speed = String.format("%.2f", Math.abs(ow.f*27)) +" MPH";
                    int x = (128/2)-24;
                    if (speed.length() == 8) {
                        x = (128/2)-21;
                    }
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, speed, x+10, 14, 0x53A1D2);
                }
            }
        }));
        FallPacket.registerPacket();
        EntityRendererRegistry.register(OneWheel.OWPE, OneWheelPlayerRender::new);
        EntityRendererRegistry.register(OneWheel.OW, OneWheelRender::new);
    }
}
