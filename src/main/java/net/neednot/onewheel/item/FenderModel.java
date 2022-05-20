package net.neednot.onewheel.item;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FenderModel extends AnimatedGeoModel<FenderItem> {
    @Override
    public Identifier getModelLocation(FenderItem object)
    {
        return new Identifier("onewheel", "geo/fender.geo.json");
    }

    @Override
    public Identifier getTextureLocation(FenderItem object)
    {
        return new Identifier("onewheel", "textures/ow.png");
    }

    @Override
    public Identifier getAnimationFileLocation(FenderItem object)
    {
        return new Identifier("onewheel", "animations/ow.animations.json");
    }
}
