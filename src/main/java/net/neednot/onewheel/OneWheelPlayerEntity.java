package net.neednot.onewheel;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        return PlayState.STOP;
    }

    public OneWheelPlayerEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }
    @Override
    public void tick() {
        super.tick();

        if (!MinecraftClient.getInstance().player.hasVehicle()) {
            this.discard();
        }
        else {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            this.setPos(player.getX(), player.getY()-0.1f, player.getZ());
            this.headYaw = player.headYaw+90;
            this.bodyYaw = player.bodyYaw+90;
            this.setYaw(player.getYaw()+90);
        }
    }
    @Override
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        this.setYaw(player.getYaw());
        this.bodyYaw = this.getYaw();
        this.headYaw = this.getYaw();
        super.updatePositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        super.updatePosition(player.getX(), player.getY(), player.getZ());
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
