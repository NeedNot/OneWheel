package net.neednot.onewheel.mixin;

import com.sun.jna.platform.win32.OaIdl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.neednot.onewheel.OneWheel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {


    @Shadow @Final public ModelPart rightLeg;

    @Shadow @Final public ModelPart leftLeg;

    @Shadow @Final public ModelPart body;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    @Inject(at = @At("TAIL"), method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V")
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (player.getMainHandStack().isItemEqual(OneWheel.oneWheel.getDefaultStack())) {

                if (player.getMainArm() == Arm.RIGHT) {
                    this.rightArm.pitch = (float) Math.toRadians(-20);
                    this.rightArm.yaw = 0f;
                }
                else {
                    this.leftArm.pitch = (float) Math.toRadians(-20);
                    this.leftArm.yaw = 0f;
                }
            }
            if (player.getOffHandStack().isItemEqual(OneWheel.oneWheel.getDefaultStack()) ) {
                if (player.getMainArm() == Arm.RIGHT) {
                    this.leftArm.pitch = (float) Math.toRadians(-20);
                    this.leftArm.yaw = 0f;
                }
                else {
                    this.rightArm.pitch = (float) Math.toRadians(-20);
                    this.rightArm.yaw = 0f;
                }
            }
        }
    }
}
