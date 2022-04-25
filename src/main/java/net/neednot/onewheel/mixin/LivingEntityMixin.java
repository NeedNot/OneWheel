package net.neednot.onewheel.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    protected Vec3d speedometer$lastPos;
    DecimalFormat df = new DecimalFormat("#.##");
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tickMovement")
    public void speedometer$beforeTickMovement(CallbackInfo info) {
        if (this.getType().getName().toString().contains("onewheel") && this.hasPassengers()) {
            speedometer$lastPos = this.getPos();
        }
    }
    @Inject(at = @At("TAIL"), method = "tickMovement")
    public void tickMovement(CallbackInfo info) {
        if (this.getType().getName().toString().contains("onewheel") && this.hasPassengers() && MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendMessage(new LiteralText(df.format((this.getPos().distanceTo(speedometer$lastPos)*20)*2.237)+" MPH"), true);
        }
    }
}
