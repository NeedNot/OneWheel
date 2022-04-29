package net.neednot.onewheel;

import net.minecraft.client.MinecraftClient;
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
            if (MinecraftClient.getInstance().player.getVehicle() instanceof OneWheelEntity) {
                OneWheelEntity ow = (OneWheelEntity) MinecraftClient.getInstance().player.getVehicle();
                float yaw = ow.yawVelocity;
                if (yaw < 0) {
                    if (bone.getName().equals("player")) {

                        bone.setRotationX((yaw * 0.005f));
                    }
                    if (bone.getName().equals("right_lower")) {

                        bone.setRotationX(invert(0.436332f));
                    }
                    if (bone.getName().equals("left_lower")) {

                        bone.setRotationX(invert(0.436332f));
                    }
                }
                if (yaw > 0) {
                    if (bone.getName().equals("player")) {

                        float muti = 0;
                        if (Math.abs(yaw) == 6.0f) {
                            muti = 0.055f;
                        } else {
                            muti = 0.15f;
                        }
                        if (yaw != 0.0f) {
                            bone.setPositionZ(-4);
                        }
                        bone.setRotationX((yaw * (muti / 5)));
                    }
                    if (bone.getName().equals("right_lower")) {
                        float muti = 0;
                        if (Math.abs(yaw) == 6.0f) {
                            muti = 0.436332f;
                        } else {
                            muti = 0.436332f;
                        }
                        bone.setRotationX(invert(muti));
                    }
                    if (bone.getName().equals("left_lower")) {
                        float muti = 0;
                        if (Math.abs(yaw) == 6.0f) {
                            muti = 0.436332f;
                        } else {
                            muti = 0.436332f;
                        }
                        bone.setRotationX(invert(muti));
                    }
                }

            }
        }
        System.out.println(alpha);
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
    @Override
    public void renderLate(OneWheelPlayerEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            animatable.setInvisible(true);
        }
        else {
            animatable.setInvisible(false);
        }
        super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }
    public float invert(float amount) {
        return amount *=-1;
    }
}
