package net.neednot.onewheel.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckStingersFeatureRenderer.class)
public abstract class StuckStingersFeatureRendererMixin<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StuckObjectsFeatureRenderer<T, M> {

    public StuckStingersFeatureRendererMixin(LivingEntityRenderer<T, M> livingEntityRenderer) {
        super(livingEntityRenderer);
    }
    @Inject(at = @At("HEAD"), method = "renderObject", cancellable = true)
    protected void renderObject(MatrixStack matrices , VertexConsumerProvider vertexConsumers , int light , Entity entity , float directionX , float directionY , float directionZ , float tickDelta , CallbackInfo ci) {
        if (entity.hasVehicle()) {
            if (entity.getVehicle() instanceof OneWheelEntity) {
                ci.cancel();
            }
        }
    }
}
