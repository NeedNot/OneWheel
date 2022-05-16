package net.neednot.onewheel.entity.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.packet.PlayerAnimToServerPacket;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OneWheelPlayerEntity extends AnimalEntity implements IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);
    public static AnimationBuilder nosedivef = new AnimationBuilder().addAnimation("animation.player.nosedivef", false);
    public static AnimationBuilder nosediveb = new AnimationBuilder().addAnimation("animation.player.nosediveb", false);
    public static AnimationBuilder mount = new AnimationBuilder().addAnimation("animation.player.mount", false);
    public static AnimationBuilder tiltf = new AnimationBuilder().addAnimation("animation.player.tiltf", false).addAnimation("animation.player.holdb", true);
    public static AnimationBuilder tiltb = new AnimationBuilder().addAnimation("animation.player.tiltb", false).addAnimation("animation.player.holdf", true);
    public static AnimationBuilder pushbackf = new AnimationBuilder().addAnimation("animation.player.pushbackf", false).addAnimation("animation.player.holdPushbackf", true);
    public static AnimationBuilder pushbackb = new AnimationBuilder().addAnimation("animation.player.pushbackb", false).addAnimation("animation.player.holdPushbackb", true);
    public static AnimationBuilder flat = new AnimationBuilder().addAnimation("animation.player.flat", true);
    public static AnimationBuilder breakingF = new AnimationBuilder().addAnimation("animation.player.breakf", false);
    public static AnimationBuilder breakingB = new AnimationBuilder().addAnimation("animation.player.breakb", false);
    public static AnimationBuilder deadmount = new AnimationBuilder().addAnimation("animation.player.deadmount", true);

    private int fails;

    public static final TrackedData<String> SYNCEDPLAYER = DataTracker.registerData(OneWheelPlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    public PlayerEntity assignedPlayer;

    public AnimationBuilder playAnimation;

    public static final Map<Integer, AnimationBuilder> getMap() {
        Map<Integer, AnimationBuilder> map = new HashMap<>();
        map.put(0, nosedivef);
        map.put(1, nosediveb);
        map.put(2, mount);
        map.put(3, tiltf);
        map.put(4, tiltb);
        map.put(5, pushbackf);
        map.put(6, pushbackb);
        map.put(7, flat);
        map.put(8, breakingF);
        map.put(9, breakingB);
        map.put(10, deadmount);
        return map;
    }

    public static AnimationBuilder getAnimation(int anim) {
        return getMap().get(anim);
    }
    public Integer getKey(Map<Integer, AnimationBuilder> map, AnimationBuilder value) {
        for (Map.Entry<Integer, AnimationBuilder> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return 2;
    }

    private void sendAnimPacket(AnimationBuilder animationBuilder) {
        int anim = getKey(getMap(), animationBuilder);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(anim);
        buf.writeInt(getId());
        buf.writeFloat(((OneWheelEntity) assignedPlayer.getVehicle()).yawVelocity);
        ClientPlayNetworking.send(PlayerAnimToServerPacket.PACKET_ID, buf);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (assignedPlayer == null) return PlayState.STOP;
        if (assignedPlayer.hasVehicle() && assignedPlayer.getUuidAsString().equals(MinecraftClient.getInstance().player.getUuidAsString())) {
            if (assignedPlayer.getVehicle() instanceof OneWheelEntity) {
                OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();
                if (ow.forcedF > 10) {
                    event.getController().animationSpeed = 4;
                    event.getController().setAnimation(nosedivef);
                    sendAnimPacket(nosedivef);
                    return PlayState.CONTINUE;
                }
                if (ow.forcedb > 10) {
                    event.getController().animationSpeed = 4;
                    event.getController().setAnimation(nosediveb);
                    sendAnimPacket(nosediveb);
                    return PlayState.CONTINUE;
                }
                if (ow.playerMount) {
                    ow.playerMount = false;
                    event.getController().setAnimation(mount);
                    sendAnimPacket(mount);
                    return PlayState.CONTINUE;
                }
                if (ow.playerDeadMount) {
                    event.getController().setAnimation(deadmount);
                    sendAnimPacket(deadmount);
                    return PlayState.CONTINUE;
                }
                if (ow.playerTiltF) {
                    event.getController().setAnimation(tiltf);
                    sendAnimPacket(tiltf);
                    return PlayState.CONTINUE;
                }
                if (ow.playerTiltB) {
                    event.getController().setAnimation(tiltb);
                    sendAnimPacket(tiltb);
                    return PlayState.CONTINUE;
                }
                if (ow.playerBreakingF) {
                    ow.playerBreakingF = false;
                    event.getController().setAnimation(breakingF);
                    sendAnimPacket(breakingF);
                    return PlayState.CONTINUE;
                }
                if (ow.playerBreakingB) {
                    ow.playerBreakingB = false;
                    event.getController().setAnimation(breakingB);
                    sendAnimPacket(breakingB);
                    return PlayState.CONTINUE;
                }
                if (ow.playerPushbackf) {
                    ow.playerPushbackf = false;
                    event.getController().setAnimation(pushbackf);
                    sendAnimPacket(pushbackf);
                    return PlayState.CONTINUE;
                }
                if (ow.playerPushbackb) {
                    ow.playerPushbackb = false;
                    event.getController().setAnimation(pushbackb);
                    sendAnimPacket(pushbackb);
                    return PlayState.CONTINUE;
                }
                if (ow.playerFlat) {
                    ow.playerFlat = false;
                    event.getController().setAnimation(flat);
                    sendAnimPacket(flat);
                    return PlayState.CONTINUE;
                }
                sendAnimPacket(null);
            }
        }
        else if (playAnimation != null) {
            if (playAnimation.equals(nosediveb)||playAnimation.equals(nosedivef)) event.getController().animationSpeed = 4;
            event.getController().setAnimation(playAnimation);
        }
        return PlayState.CONTINUE;
    }

    public OneWheelPlayerEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }
    @Override
    public void initDataTracker() {
        dataTracker.startTracking(SYNCEDPLAYER, "nothing");
        super.initDataTracker();
    }
    public void setSyncedplayer(String string) {
        dataTracker.set(SYNCEDPLAYER, string);
        setAssignedPlayer();
    }
    @Override
    public void tick() {
        super.tick();
        if (world.isClient()) System.out.println("ticking");
        if (assignedPlayer != null) {
            if (!assignedPlayer.hasVehicle()) {
                fails += 1;
                if (fails > 2) {
                    System.out.println("discarding");
                    this.discard();
                }
            } else if (assignedPlayer.getVehicle() instanceof OneWheelEntity) {
                OneWheelEntity ow = (OneWheelEntity) assignedPlayer.getVehicle();

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
        } else {
            fails += 1;
            System.out.println(fails);
            if (fails > 2) this.discard();
        }
    }

    public void setAssignedPlayer() {
        if (!dataTracker.get(SYNCEDPLAYER).equals("nothing")) {
            UUID uuid = UUID.fromString(dataTracker.get(SYNCEDPLAYER));
            assignedPlayer = world.getPlayerByUuid(uuid);
        }
    }

    @Override
    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        if (assignedPlayer == null) {
            super.updatePositionAndAngles(x, y, z, yaw, pitch);
            return;
        }
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
        if (assignedPlayer == null) {
            super.updatePosition(x, y, z);
            return;
        }
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
