package net.neednot.onewheel.entity.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OneWheelPlayerRender extends GeoEntityRenderer<OneWheelPlayerEntity> {
    float yaw = 90;
    float realyaw;
    boolean isRiding;
    public OneWheelPlayerRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OneWheelPlayerModel());
    }
    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (MinecraftClient.getInstance().player.hasVehicle()) {
            if (bone.getName().equals("heads")) {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                OneWheelEntity ow = (OneWheelEntity) player.getVehicle();
                float yaw = MathHelper.wrapDegrees(player.getYaw()-ow.getYaw()+90);
                yaw = MathHelper.clamp(yaw, -90, 90);
                bone.setRotationY(invert((float) Math.toRadians((double) yaw)));
                bone.setRotationX(invertif((float) Math.toRadians((double) player.getPitch())));
            }
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
                float oyaw = invert(360-(MathHelper.wrapDegrees(ow.getYaw())))%360;
                float speed = ow.f;
                float fakeyaw = yaw;
                if (speed < 0) {
                    fakeyaw = invert(yaw);
                }

                if (bone.getName().equals("player")) {
                    if (ow.forcedb > 0 || ow.forcedF > 0) {
                        ow.bonePos = new Vec3d(bone.getPositionX()/12, bone.getPositionY()/16, bone.getPositionZ()/-12).rotateY((float) Math.toRadians(Math.abs(oyaw-90))).add(ow.getPos());
                    }
                }
                if (fakeyaw < 0) {
                    if (bone.getName().equals("player")) {
                        bone.setPositionZ(-3);
                        bone.setPositionY(-1f);
                        bone.setRotationX((yaw * 0.005f));
                    }
                    if (bone.getName().equals("right_lower")) {

                        bone.setRotationX(invert(0.436332f));
                    }
                    if (bone.getName().equals("left_lower")) {

                        bone.setRotationX(invert(0.436332f));
                    }
                }
                if (fakeyaw > 0) {
                    if (bone.getName().equals("player")) {

                        float muti = 0;
                        if (Math.abs(yaw) == 6.0f) {
                            muti = 0.055f;
                        } else {
                            muti = 0.15f;
                        }
                        if (yaw != 0.0f) {
                            bone.setPositionZ(0);
                        }
                        bone.setPositionY(-1f);
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
        if (!MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

    private float invertDeg(float yaw) {
        if (yaw > 0) {
            return yaw-360;
        }
        return yaw;
    }

    private float invertif(float toRadians) {
        if (!MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            return toRadians *= -1;
        } return toRadians;
    }
    @Override
    public void renderLate(OneWheelPlayerEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.realyaw = MathHelper.wrapDegrees(animatable.bodyYaw);
        super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }

    public float invert(float amount) {
        return amount *=-1;
    }
}
