package net.neednot.onewheel.entity.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class OneWheelPlayerArmorRenderer extends ExtendedGeoEntityRenderer<OneWheelPlayerEntity> {


    protected OneWheelPlayerArmorRenderer(EntityRendererFactory.Context renderManager , AnimatedGeoModel<OneWheelPlayerEntity> modelProvider) {
        super(renderManager , modelProvider);
    }

    @Override
    protected ItemStack getArmorForBone(String boneName, OneWheelPlayerEntity currentEntity) {
        //code here
        return null;
    }

    @Nullable
    @Override
    protected Identifier getTextureForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        return null;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem , String boneName) {
        return null;
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        return null;
    }

    @Override
    protected void preRenderItem(MatrixStack matrixStack , ItemStack item , String boneName , OneWheelPlayerEntity currentEntity , IBone bone) {

    }

    @Override
    protected void preRenderBlock(BlockState block , String boneName , OneWheelPlayerEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(MatrixStack matrixStack , ItemStack item , String boneName , OneWheelPlayerEntity currentEntity , IBone bone) {

    }

    @Override
    protected void postRenderBlock(BlockState block , String boneName , OneWheelPlayerEntity currentEntity) {

    }
}
