package net.neednot.onewheel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class OneWheelPlayerModel extends AnimatedGeoModel<OneWheelPlayerEntity> {

    @Override
    public Identifier getModelLocation(OneWheelPlayerEntity object)
    {
        return new Identifier("player", "geo/player.geo.json");
    }

    @Override
    public Identifier getTextureLocation(OneWheelPlayerEntity object)
    {
        return MinecraftClient.getInstance().player.getSkinTexture();
    }

    @Override
    public Identifier getAnimationFileLocation(OneWheelPlayerEntity object)
    {
        return new Identifier("player", "animations/model.animation.json");
    }
}