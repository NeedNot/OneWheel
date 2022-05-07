package net.neednot.onewheel.entity.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class OneWheelPlayerModel extends AnimatedGeoModel<OneWheelPlayerEntity> {

    @Override
    public Identifier getModelLocation(OneWheelPlayerEntity object)
    {
        if (MinecraftClient.getInstance().player.getModel().equals("default")) {
            return new Identifier("player" , "geo/player.geo.json");
        }
        else {
            return new Identifier("player" , "geo/alex.geo.json");
        }
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