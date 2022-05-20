package net.neednot.onewheel.entity.board;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.board.OneWheelModel;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.Objects;

public class OneWheelRender extends GeoEntityRenderer<OneWheelEntity> {
    float yaw;
    float x = 0f;
    float speed;
    OneWheelEntity ow;
    public OneWheelRender(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OneWheelModel());
    }
    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (bone.getName().equals("onewheel")) {
            float muti;
            if (speed < 0) {
                yaw = invert(yaw);
            }
            if (Math.abs(this.yaw) == 6.0f) {
                muti = 0.055f;
            }
            else {
                muti = 0.15f;
            }
            bone.setRotationZ(invert(this.yaw*muti));
        }
        if (ow != null) if (bone.getName().contains("fender")) {
            bone.setHidden(!ow.hasFender());
        }
        if (bone.getName().equals("wheel")) {
            if (this.yaw == 6.0f) {
                bone.setRotationX(x += 0.01f);
            }
            if (this.yaw == -6.0f) {
                bone.setRotationX(x -= 0.01f);
            }
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
    @Override
    public void renderLate(OneWheelEntity animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.yaw = animatable.yawVelocity;
        this.speed = animatable.f;
        ow = animatable;
        if (animatable.getControllingPassenger() != null) {
            if (!animatable.getControllingPassenger().getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                yaw = animatable.fakeYawVelocity;
                speed = animatable.fakeSpeed;
            }
        }
        super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }
    public Float invert(float f) {
        return f *=-1;
    }
}
