package net.neednot.onewheel;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class OneWheelPlayerEntity extends AnimalEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);
    public AnimationBuilder nosedivef = new AnimationBuilder().addAnimation("animation.player.nosedivef", false);
    public AnimationBuilder nosediveb = new AnimationBuilder().addAnimation("animation.player.nosediveb", false);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player.hasVehicle()) {
            if (player.getVehicle() instanceof OneWheelEntity) {
                OneWheelEntity ow = (OneWheelEntity) player.getVehicle();
                if (ow.forcedF > 10) {
                    event.getController().animationSpeed = 4;
                    event.getController().setAnimation(nosedivef);
                    return PlayState.CONTINUE;
                }
                if (ow.forcedb > 10) {
                    event.getController().animationSpeed = 4;
                    event.getController().setAnimation(nosediveb);
                    return PlayState.CONTINUE;
                }
            }
        }
        return PlayState.STOP;
    }

    public OneWheelPlayerEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }
    @Override
    public void tick() {
        super.tick();
        if (MinecraftClient.getInstance().player != null) {
            if (!MinecraftClient.getInstance().player.hasVehicle()) {
                this.discard();
            } else {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                OneWheelEntity ow = (OneWheelEntity) player.getVehicle();
                if (ow.forcedb == 25 || ow.forcedF == 25) {
                    this.setHealth(player.getHealth());
                    this.damage(DamageSource.FALL , 5f);
                }
                if (ow.forcedb == 0 && ow.forcedF == 0) {
                    this.headYaw = player.headYaw - 90;
                    this.bodyYaw = player.bodyYaw - 90;
                    this.setYaw(player.getYaw() - 90);
                    this.setPos(player.getX() , player.getY() - 0.1f , player.getZ());
                } else {
                    this.headYaw = ow.headYaw - 90;
                    this.bodyYaw = ow.bodyYaw - 90;
                    this.setYaw(ow.getYaw() - 90);
                    this.setPos(ow.getX() , ow.getY() , ow.getZ());
                }
            }
        }
    }
    @Override
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        OneWheelEntity ow = (OneWheelEntity) player.getVehicle();
        this.setYaw(player.getYaw());
        this.bodyYaw = this.getYaw();
        this.headYaw = this.getYaw();
        if (ow.forcedb > 0|| ow.forcedF > 0) {
            this.setYaw(ow.getYaw()+90);
        }
        super.updatePositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        OneWheelEntity ow = (OneWheelEntity) player.getVehicle();
        if (ow.forcedb == 0 && ow.forcedF == 0) {
            super.updatePosition(player.getX() , player.getY() , player.getZ());
        }
        else {
            super.updatePosition(ow.getX() , ow.getY() , ow.getZ());
        }
        super.updatePosition(player.getX() , player.getY() , player.getZ());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengerList().isEmpty() ? null : this.getPassengerList().get(0);
    }

    @Override
    public boolean canBeControlledByRider() {
        return true;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<OneWheelPlayerEntity>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

}
