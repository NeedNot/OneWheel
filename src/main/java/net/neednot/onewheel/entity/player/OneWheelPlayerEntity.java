package net.neednot.onewheel.entity.player;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.packet.AssignPlayerPacket;
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
    public AnimationBuilder mount = new AnimationBuilder().addAnimation("animation.player.mount", false);
    public AnimationBuilder tiltf = new AnimationBuilder().addAnimation("animation.player.tiltf", false).addAnimation("animation.player.holdb", true);
    public AnimationBuilder tiltb = new AnimationBuilder().addAnimation("animation.player.tiltb", false).addAnimation("animation.player.holdf", true);
    public AnimationBuilder pushbackf = new AnimationBuilder().addAnimation("animation.player.pushbackf", false).addAnimation("animation.player.holdPushbackf", true);
    public AnimationBuilder pushbackb = new AnimationBuilder().addAnimation("animation.player.pushbackb", false).addAnimation("animation.player.holdPushbackb", true);
    public AnimationBuilder flat = new AnimationBuilder().addAnimation("animation.player.flat", true);
    public AnimationBuilder breakingF = new AnimationBuilder().addAnimation("animation.player.breakf", false);
    public AnimationBuilder breakingB = new AnimationBuilder().addAnimation("animation.player.breakb", false);
    public AnimationBuilder deadmount = new AnimationBuilder().addAnimation("animation.player.deadmount", true);

    public PlayerEntity assignedPlayer;

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (assignedPlayer.hasVehicle()) {
            if (assignedPlayer.getVehicle() instanceof OneWheelEntity) {
                OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();
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
                if (ow.playerMount) {
                    ow.playerMount = false;
                    event.getController().setAnimation(mount);
                    return PlayState.CONTINUE;
                }
                if (ow.playerDeadMount) {
                    event.getController().setAnimation(deadmount);
                    return PlayState.CONTINUE;
                }
                if (ow.playerTiltF) {
                    event.getController().setAnimation(tiltf);
                    return PlayState.CONTINUE;
                }
                if (ow.playerTiltB) {
                    event.getController().setAnimation(tiltb);
                    return PlayState.CONTINUE;
                }
                if (ow.playerBreakingF) {
                    ow.playerBreakingF = false;
                    event.getController().setAnimation(breakingF);
                    return PlayState.CONTINUE;
                }
                if (ow.playerBreakingB) {
                    ow.playerBreakingB = false;
                    event.getController().setAnimation(breakingB);
                    return PlayState.CONTINUE;
                }
                if (ow.playerPushbackf) {
                    ow.playerPushbackf = false;
                    event.getController().setAnimation(pushbackf);
                    return PlayState.CONTINUE;
                }
                if (ow.playerPushbackb) {
                    ow.playerPushbackb = false;
                    System.out.println(event.getController().getCurrentAnimation().animationName);
                    event.getController().setAnimation(pushbackb);
                    return PlayState.CONTINUE;
                }
                if (ow.playerFlat) {
                    ow.playerFlat = false;
                    event.getController().setAnimation(flat);
                    return PlayState.CONTINUE;
                }
            }
        }
        return PlayState.CONTINUE;
    }

    public OneWheelPlayerEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }
    public void setAssignedPlayer(PlayerEntity player) {

        this.assignedPlayer = MinecraftClient.getInstance().player;
        if (!world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(player.getUuid());
            buf.writeInt(getId());
            ServerPlayNetworking.send((ServerPlayerEntity) player, AssignPlayerPacket.PACKET_ID, buf);
        }
        else {
            System.out.println("i got it");
        }
    }
    @Override
    public void tick() {
        super.tick();
        System.out.println(getUuid());
        if (assignedPlayer != null) {
            if (!assignedPlayer.hasVehicle()) {
                this.discard();
            } else {
                OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();
                setLeftHanded(assignedPlayer.getMainArm().equals(Arm.LEFT));
                equipStack(EquipmentSlot.MAINHAND, assignedPlayer.getMainHandStack());
                equipStack(EquipmentSlot.OFFHAND, assignedPlayer.getOffHandStack());
                equipStack(EquipmentSlot.HEAD, assignedPlayer.getEquippedStack(EquipmentSlot.HEAD));
                equipStack(EquipmentSlot.CHEST, assignedPlayer.getEquippedStack(EquipmentSlot.CHEST));
                equipStack(EquipmentSlot.LEGS, assignedPlayer.getEquippedStack(EquipmentSlot.LEGS));
                equipStack(EquipmentSlot.FEET, assignedPlayer.getEquippedStack(EquipmentSlot.FEET));
                if (ow.forcedb == 25 || ow.forcedF == 25) {
                    this.setHealth(assignedPlayer.getHealth());
                    this.damage(DamageSource.FALL , 5f);
                }
                if (ow.forcedb == 0 && ow.forcedF == 0) {
                    this.headYaw = assignedPlayer.headYaw - 90;
                    this.bodyYaw = assignedPlayer.bodyYaw - 90;
                    this.setYaw(assignedPlayer.getYaw() - 90);
                    this.setPos(assignedPlayer.getX() , assignedPlayer.getY() - 0.1f , assignedPlayer.getZ());
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
        OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();
        this.setYaw(assignedPlayer.getYaw());
        this.bodyYaw = this.getYaw();
        this.headYaw = this.getYaw();
        if (ow.forcedb > 0|| ow.forcedF > 0) {
            this.setYaw(ow.getYaw()+90);
        }
        super.updatePositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
    }

    @Override
    public void updatePosition(double x, double y, double z) {
        OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();
        if (ow.forcedb == 0 && ow.forcedF == 0) {
            super.updatePosition(assignedPlayer.getX() , assignedPlayer.getY() , assignedPlayer.getZ());
        }
        else {
            super.updatePosition(ow.getX() , ow.getY() , ow.getZ());
        }
        super.updatePosition(assignedPlayer.getX() , assignedPlayer.getY() , assignedPlayer.getZ());
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
