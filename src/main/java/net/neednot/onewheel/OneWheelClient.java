package net.neednot.onewheel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.board.OneWheelRender;
import net.neednot.onewheel.entity.player.OneWheelPlayerRender;
import net.neednot.onewheel.item.FenderRender;
import net.neednot.onewheel.item.ItemRegister;
import net.neednot.onewheel.packet.*;
import net.neednot.onewheel.ui.WorkBenchScreen;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class OneWheelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemRegister.registerOneWheelItem();
        HudRenderCallback.EVENT.register(((matrixStack, tickDelta) -> {
            Entity entity;
            int scaledwidth;
            int scaledheight;
            if ((entity = MinecraftClient.getInstance().player.getVehicle()) != null) {
                if (entity instanceof OneWheelEntity) {
                    OneWheelEntity ow = (OneWheelEntity) entity;
                    scaledwidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
                    scaledheight = MinecraftClient.getInstance().getWindow().getScaledHeight();

                    //battery percent string
                    int color = 0xFF0000;
                    if ((ow.battery/32186.88f)*100 > 33) color = 0xffff00;
                    if ((ow.battery/32186.88f)*100 > 66) color = 0x32CD32;
                    String percent = String.format("%.2f", (ow.battery/32186.88f)*100) +"%";
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, percent, scaledwidth/2+95, scaledheight-15, color);

                    //speed mph string
                    String speed = String.format("%.2f", Math.abs(ow.f*27)) +" MPH";
                    int x = (speed.length() * 3)-1;
                    MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, speed, scaledwidth/2-x, scaledheight-69, 0x0f81c9);
                }
            }
        }));
        BlockRenderLayerMap.INSTANCE.putBlock(OneWheel.WORKBENCH, RenderLayer.getCutout());
        ScreenRegistry.register(OneWheel.WORK_BENCH_SCREEN_HANDLER, WorkBenchScreen::new);
        GeoItemRenderer.registerItemRenderer(OneWheel.fender, new FenderRender());
        BoardAnimToClientPacket.registerPacket();
        PlayerAnimToClientPacket.registerPacket();
        SpawnFakePlayerPacket.registerPacket();
        FallPacket.registerPacket();
        BatteryPacket.registerPacket();
        EntityRendererRegistry.register(OneWheel.OWPE, OneWheelPlayerRender::new);
        EntityRendererRegistry.register(OneWheel.OW, OneWheelRender::new);
    }
}
