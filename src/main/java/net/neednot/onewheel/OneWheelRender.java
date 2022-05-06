package net.neednot.onewheel;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class OneWheelRender extends GeoEntityRenderer<OneWheelEntity> {
    float yaw;
    float x = 0f;
    float speed;
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
        if (bone.getName().equals("wheel")) {
            if (this.yaw == 6.0f) {
                bone.setRotationX(x += 0.01f);
                System.out.println(bone.getRotationX());
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
        super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }
    public Float invert(float f) {
        return f *=-1;
    }
}
