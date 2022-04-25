package net.neednot.onewheel;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

public class OneWheelModel extends AnimatedGeoModel<OneWheelEntity> {

    @Override
    public Identifier getModelLocation(OneWheelEntity object)
        {
        return new Identifier("onewheel", "geo/ow.geo.json");
    }

    @Override
    public Identifier getTextureLocation(OneWheelEntity object)
    {
        return new Identifier("onewheel", "textures/"+object.getColor()+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(OneWheelEntity object)
    {
        return new Identifier("onewheel", "animations/ow.animation.json");
    }
}
