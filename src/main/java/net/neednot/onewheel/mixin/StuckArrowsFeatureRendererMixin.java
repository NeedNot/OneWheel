package net.neednot.onewheel.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckArrowsFeatureRenderer.class)
public abstract class StuckArrowsFeatureRendererMixin<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StuckObjectsFeatureRenderer<T, M> {

    public StuckArrowsFeatureRendererMixin(EntityRendererFactory.Context context, LivingEntityRenderer<T, M> entityRenderer) {
        super(entityRenderer);
    }

    @Inject(at = @At("HEAD"), method = "renderObject", cancellable = true)
    protected void renderObject(MatrixStack matrices , VertexConsumerProvider vertexConsumers , int light , Entity entity , float directionX , float directionY , float directionZ , float tickDelta , CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.hasVehicle()) {
            if (MinecraftClient.getInstance().player.getVehicle() instanceof OneWheelEntity) {
                ci.cancel();
            }
        }
    }
}
