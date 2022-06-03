package net.neednot.onewheel.entity.player;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.fabricmc.loader.impl.game.GameProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class OneWheelPlayerModel extends AnimatedGeoModel<OneWheelPlayerEntity> {

    @Override
    public Identifier getModelLocation(OneWheelPlayerEntity object) {
        if (object.assignedPlayer == null) return new Identifier("player" , "geo/alex.geo.json");
        if (((AbstractClientPlayerEntity) object.assignedPlayer).getModel().equals("default")) {
            return new Identifier("player" , "geo/player.geo.json");
        }
        else {
            return new Identifier("player" , "geo/alex.geo.json");
        }
    }

    @Override
    public Identifier getTextureLocation(OneWheelPlayerEntity object)
    {
        if (object.assignedPlayer == null) return DefaultSkinHelper.getTexture(MinecraftClient.getInstance().player.getUuid());
        return ((AbstractClientPlayerEntity) object.assignedPlayer).getSkinTexture();
    }

    @Override
    public Identifier getAnimationFileLocation(OneWheelPlayerEntity object)
    {
        return new Identifier("player", "animations/model.animation.json");
    }
}