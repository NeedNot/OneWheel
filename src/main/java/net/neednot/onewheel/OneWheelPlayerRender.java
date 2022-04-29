package net.neednot.onewheel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
        if (MinecraftClient.getInstance().player.hasVehicle()) {
            if (bone.getName().equals("right_arms")) {
                bone.setRotationZ(0.785398f);
            }
            if (bone.getName().equals("left_arms")) {
                bone.setRotationZ(-0.785398f);
            }
            if (bone.getName().equals("right_legs")) {
                bone.setRotationZ(0.349066f);
            }
            if (bone.getName().equals("left_legs")) {
                bone.setRotationZ(-0.349066f);
            }
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
