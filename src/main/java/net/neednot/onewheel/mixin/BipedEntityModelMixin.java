package net.neednot.onewheel.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
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

        if (this.riding) {
            this.rightLeg.pitch = 0f;
            this.leftLeg.pitch = 0F;
            this.leftLeg.yaw = -0f;
            this.rightLeg.yaw = 0f;

            this.rightLeg.roll = 50;
            this.leftLeg.roll = -50;

            this.leftArm.pitch = 0f;
            this.rightArm.pitch = 0f;
            this.leftArm.yaw = 0f;
            this.rightArm.yaw = 0f;

            this.rightArm.roll = 165f;
            this.leftArm.roll = -165f;
        }
    }

}
