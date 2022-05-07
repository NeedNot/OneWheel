package net.neednot.onewheel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.neednot.onewheel.entity.board.OneWheelRender;
import net.neednot.onewheel.entity.player.OneWheelPlayerRender;
import net.neednot.onewheel.packet.FallPacket;

public class OneWheelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FallPacket.registerPacket();
        EntityRendererRegistry.register(OneWheel.OWPE, OneWheelPlayerRender::new);
        EntityRendererRegistry.register(OneWheel.OW, OneWheelRender::new);
    }
}
