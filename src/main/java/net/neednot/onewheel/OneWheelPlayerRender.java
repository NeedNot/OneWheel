package net.neednot.onewheel;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OneWheelPlayerRender extends GeoEntityRenderer<OneWheelPlayerEntity> {
    float yaw = 90;
    boolean isRiding;
    public OneWheelPlayerRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OneWheelPlayerModel());
    }
    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("player")) {

            bone.setRotationY(yaw);
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
