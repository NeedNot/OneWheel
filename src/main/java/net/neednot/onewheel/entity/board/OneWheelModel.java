package net.neednot.onewheel.entity.board;

import net.minecraft.util.Identifier;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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
