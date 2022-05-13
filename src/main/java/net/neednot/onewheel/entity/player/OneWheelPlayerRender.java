package net.neednot.onewheel.entity.player;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

import java.util.List;

public class OneWheelPlayerRender extends ExtendedGeoEntityRenderer<OneWheelPlayerEntity> {
    float yaw = 90;
    float realyaw;
    boolean canRender;
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
                        stack.scale(0.9375f, 0.9375f, 0.9375f);
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
        if (canRender) {
            super.renderRecursively(bone , stack , bufferIn , packedLightIn , packedOverlayIn , red , green , blue , alpha);
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
        stackIn.scale(0.9375f,0.9375f,0.9375f);
        if (animatable.assignedPlayer != null) {
            canRender = false;
            if (animatable.assignedPlayer.getUuidAsString().equals(MinecraftClient.getInstance().player.getUuidAsString())) {
                MinecraftClient player = MinecraftClient.getInstance();
                if (!player.options.getPerspective().isFirstPerson()) {
                    canRender = true;
                }
            }
            else {
                canRender = true;
            }
        }
        super.renderLate(animatable , stackIn , ticks , renderTypeBuffer , bufferIn , packedLightIn , packedOverlayIn , red , green , blue , partialTicks);
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return true;
    }

    //armor stuff
    @Override
    protected ItemStack getArmorForBone(String boneName, OneWheelPlayerEntity currentEntity) {
        switch (boneName) {
            case "armorLeftArms":
            case "armorRightArms":
            case "armorTorso":
                return chestplate;
            case "armorHead":
                return helmet;
            case "armorLegR":
            case "armorLeggings":
            case "armorLowerLegR":
            case "armorLegL":
                return leggings;
//            case "armorLowerLegR":
//                return boots;

        }
        return null;
    }

    @Override
    protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, OneWheelPlayerEntity currentEntity) {
        switch (boneName) {
            case "armorRightArms":
            case "armorLeftArms":
            case "armorTorso":
                return EquipmentSlot.CHEST;
            case "armorHead":
                return EquipmentSlot.HEAD;
            case "armorLegR":
            case "armorLeggings":
            case "armorLowerLegR":
            case "armorLegL":
                return EquipmentSlot.LEGS;
//            case "armorLowerLegR":
//                return EquipmentSlot.FEET;
            default:
                return null;
        }
    }

    @Override
    protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
        switch (name) {
            case "armorRightArms":
                return armorModel.rightArm;
            case "armorLeftArms":
                return armorModel.leftArm;
            case "armorHead":
                return armorModel.head;
            case "armorTorso":
            case "armorLeggings":
                return armorModel.body;
            case "armorLegR":
            case "armorLowerLegR":
                return armorModel.rightLeg;
            case "armorLegL":
                return armorModel.leftLeg;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    protected Identifier getTextureForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        switch (boneName) {
            case "left_arms":
                return currentEntity.isLeftHanded() ? mainHand : offHand;
            case "right_arms":
                return currentEntity.isLeftHanded() ? offHand : mainHand;
        }
        return null;
    }

    @Override
    protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
        switch (boneName) {
            case "left_arms":
            case "right_arms":
                return ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
            default:
                return ModelTransformation.Mode.NONE;
        }
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName , OneWheelPlayerEntity currentEntity) {
        return null;
    }

    protected void preRenderItem(MatrixStack stack, ItemStack item, String boneName, OneWheelPlayerEntity currentEntity, IBone bone) {
        if(item == this.mainHand || item == this.offHand) {
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
            boolean shieldFlag = item.getItem() instanceof ShieldItem;
            if(item == this.mainHand) {
                if(shieldFlag) {
                    stack.translate(0.0, 0.125, -0.25);
                }
            } else {
                if(shieldFlag) {
                    stack.translate(0, 0.125, 0.25);
                    stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                }
            }
        }
    }

    @Override
    protected void preRenderBlock(BlockState block , String boneName , OneWheelPlayerEntity currentEntity) {

    }

    @Override
    protected void postRenderItem(MatrixStack matrixStack , ItemStack item , String boneName , OneWheelPlayerEntity currentEntity , IBone bone) {

    }

    @Override
    protected void postRenderBlock(BlockState block , String boneName , OneWheelPlayerEntity currentEntity) {

    }
    public float invert(float amount) {
        return amount *=-1;
    }
}
