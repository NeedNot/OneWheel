package net.neednot.onewheel.mixin;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.util.BipedAccessor;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Debug(export = true)
@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead, BipedAccessor {


    private final BipedEntityModel.ArmPose rightArmPose;
    private final BipedEntityModel.ArmPose leftArmPose;
    private final ModelPart head;
    private final ModelPart hat;
    @Shadow @Final public ModelPart rightLeg;

    @Shadow @Final public ModelPart leftLeg;

    @Shadow @Final public ModelPart body;

    @Shadow @Final public ModelPart leftArm;

    @Shadow @Final public ModelPart rightArm;

    public final ModelPart rightLegLower;
    public final ModelPart leftLegLower;

    public BipedEntityModelMixin(ModelPart root, Function<Identifier, RenderLayer> renderLayerFactory) {
        super(renderLayerFactory, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.head = root.getChild("head");
        this.hat = root.getChild("hat");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightLegLower = root.getChild("right_leg_lower");
        this.leftLegLower = root.getChild("left_leg_lower");
    }

    @Inject(at = @At("RETURN"), cancellable = true, method = "getModelData")
    private static void getModelData(Dilation dilation, float pivotOffsetY, CallbackInfoReturnable<ModelData> cr) {
        ModelData modelData = cr.getReturnValue();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("left_leg_lower", ModelPartBuilder.create().uv(0, 8).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, dilation), ModelTransform.pivot(1.9f, 6.0f + pivotOffsetY, 0.0f));
        modelPartData.addChild("right_leg_lower", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, dilation), ModelTransform.pivot(-1.9F, 6.0f + pivotOffsetY, 0.0F));
        cr.setReturnValue(modelData);
    }

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
    @Override
    public ModelPart getL() {
        return leftLegLower;
    }
    @Override
    public ModelPart getR() {
        return rightLegLower;
    }
}
