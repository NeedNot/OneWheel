package net.neednot.onewheel.entity.board;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;
import net.neednot.onewheel.packet.InputPacket;
import net.neednot.onewheel.packet.SpawnFakePlayerPacket;
import net.neednot.onewheel.ui.WarningScreen;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;


public class OneWheelEntity extends AnimalEntity implements IAnimatable {

    public boolean pressingLeft;
    public boolean pressingRight;
    public boolean pressingForward;
    public boolean pressingBack;
    public boolean pressingShift;
    public boolean pressingCtrl;
    public boolean mount;
    public boolean breakingf;
    public boolean breakingb;
    public boolean noseDivingf;
    public boolean noseDivingb;
    public int forcedF;
    public int forcedb;
    public float yawVelocity;
    public float f = 0.0F;
    public float prevF;
    public float prevbd;
    public float prevfd;
    public String color = "ow";
    public boolean ghost;
    public boolean offset;
    public float fdecay = 1f;
    public float bdecay = 1f;
    public float battery = 0;
    public int odds = 350;
    public float prevprevf;
    public float needSpeed = 1.11f;
    public boolean waterproof;

    public boolean playerMount;
    public boolean playerTiltF;
    public boolean playerTiltB;
    public boolean playerPushbackf;
    public boolean playerPushbackb;
    public boolean playerFlat;
    public boolean playerBreakingF;
    public boolean playerBreakingB;
    public boolean playerDeadMount;

    private static final TrackedData<String> nbtcolor = DataTracker.registerData(OneWheelEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Float> nbtbattery = DataTracker.registerData(OneWheelEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> nbtwaterproof = DataTracker.registerData(OneWheelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> nbtwaterdamage = DataTracker.registerData(OneWheelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private AnimationFactory factory = new AnimationFactory(this);

    public AnimationBuilder forward = new AnimationBuilder().addAnimation("animation.ow.forward" , true);
    public AnimationBuilder backward = new AnimationBuilder().addAnimation("animation.ow.backward" , true);
    public AnimationBuilder tiltf = new AnimationBuilder().addAnimation("animation.ow.tiltf" , false).addAnimation("animation.ow.holdb", true);
    public AnimationBuilder tiltb = new AnimationBuilder().addAnimation("animation.ow.tiltb" , false).addAnimation("animation.ow.holdf", true);
    public AnimationBuilder idle = new AnimationBuilder().addAnimation("animation.ow.idle" , true);
    public AnimationBuilder mounta = new AnimationBuilder().addAnimation("animation.ow.mount" , false);
    public AnimationBuilder flat = new AnimationBuilder().addAnimation("animation.ow.flat", true);
    public AnimationBuilder holdf = new AnimationBuilder().addAnimation("animation.ow.holdf", true);
    public AnimationBuilder holdb = new AnimationBuilder().addAnimation("animation.ow.holdb", true);
    public AnimationBuilder dismount = new AnimationBuilder().addAnimation("animation.ow.dismount" , false).addAnimation("animation.ow.idle", true);
    public AnimationBuilder pushbackf = new AnimationBuilder().addAnimation("animation.ow.pushbackf", false).addAnimation("animation.ow.holdf", true);
    public AnimationBuilder pushbackb = new AnimationBuilder().addAnimation("animation.ow.pushbackb", false).addAnimation("animation.ow.holdb", true);
    public AnimationBuilder nosedivef = new AnimationBuilder().addAnimation("animation.ow.nosedivef", true);
    public AnimationBuilder nosediveb = new AnimationBuilder().addAnimation("animation.ow.nosediveb", true);
    public Vec3d bonePos = this.getPos();
    public Random rand = new Random();
    private boolean waterdamage;


    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().transitionLengthTicks = 0F;

        if (this.hasPassengers()) {
            if (f > 0) {
                event.getController().setAnimationSpeed(f*2.3f);
                event.getController().setAnimation(forward);
                return PlayState.CONTINUE;
            }
            if (f < 0) {
                event.getController().setAnimationSpeed(Math.abs(f*2.3f));
                event.getController().setAnimation(backward);
                return PlayState.CONTINUE;
            }
        }
        else {
            return PlayState.STOP;
        }
        return PlayState.STOP;
    }
//        if (event.getController().getCurrentAnimation() != null) {
//            if (event.getController().getCurrentAnimation().animationName.equals("animation.ow.dismount")) {
//                event.getController().setAnimation(idle);
//                System.out.println(idle.getRawAnimationList().get(0).animationName);
//                return PlayState.CONTINUE;
//            }
//        }
//    }

    private <E extends IAnimatable> PlayState tilt(AnimationEvent<E> event) {
        String name;
        try {
            name = event.getController().getCurrentAnimation().animationName;
            //System.out.println("tilt "+name);
        } catch (NullPointerException e) {
            name = "null";
        }
        playerTiltB = false;
        playerTiltF = false;
        if (noseDivingf && !(f == 0)) {
            event.getController().setAnimationSpeed(2.5);
            event.getController().setAnimation(nosedivef);
            return PlayState.CONTINUE;
        }
        if (noseDivingb && !(f == 0)) {
            event.getController().setAnimationSpeed(2.5);
            event.getController().setAnimation(nosediveb);
            return PlayState.CONTINUE;
        }

        if (f > 0.66f) {
            event.getController().setAnimation(pushbackf);
            playerPushbackf = true;
            return PlayState.CONTINUE;
        }
        if (f < -0.66f) {
            event.getController().setAnimation(pushbackb);
            playerPushbackb = true;
            return PlayState.CONTINUE;
        }

        if (breakingf) {
            playerBreakingF = true;
            event.getController().setAnimation(holdf);
            return PlayState.CONTINUE;
        }
        if (breakingb) {
            playerBreakingB = true;
            event.getController().setAnimation(holdb);
            return PlayState.CONTINUE;
        }
        if (f < 0.66f && f > 0) {
            event.getController().setAnimation(tiltf);
            playerTiltF = true;
            return PlayState.CONTINUE;
        }

        if (f > -0.66f && f < 0) {
            event.getController().setAnimation(tiltb);
            playerTiltB = true;
            return PlayState.CONTINUE;
        }

        if (!this.hasPassengers()) {
            event.getController().setAnimation(dismount);
            return PlayState.CONTINUE;
        }
        if (mount) {
            if (battery > 0) {
                event.getController().setAnimation(mounta);
                playerMount = true;
                mount = false;
                playerDeadMount = false;
                return PlayState.CONTINUE;
            }
            playerDeadMount = true;
        }
        if (f == 0 && !name.contains("mount") && battery > 0) {
            playerFlat = true;
            event.getController().setAnimation(flat);
            return PlayState.CONTINUE;
        }


        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();
        setWaterProof(waterproof);
        setBattery(battery);
        if ((isSubmergedInWater() || isInLava()) && !isWaterproof() && !isWaterDamaged()) {
            setWaterDamage(true);
        }
        if (battery < 0.000001) setBattery(0);
        if (battery > 32186.88f) setBattery(32186.88f);
        if (forcedb == 0 && forcedF == 0) bonePos = this.getPos();
        BlockPos pos = this.getBlockPos();
        if (world.getReceivedRedstonePower(pos) > 0) {
            setBattery(battery+0.22352f);
        }
        if (isWaterDamaged()) {
            setBattery(0);
        }
    }

    public OneWheelEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        pressingShift = player.isSneaking();
        ItemStack black_dye = new ItemStack(Items.BLACK_DYE);
        ItemStack red_dye = new ItemStack(Items.RED_DYE);
        ItemStack blue_dye = new ItemStack(Items.BLUE_DYE);
        ItemStack brown_dye = new ItemStack(Items.BROWN_DYE);
        ItemStack green_dye = new ItemStack(Items.GREEN_DYE);
        ItemStack white_dye = new ItemStack(Items.WHITE_DYE);
        ItemStack yellow_dye = new ItemStack(Items.YELLOW_DYE);
        ItemStack lightblue_dye = new ItemStack(Items.LIGHT_BLUE_DYE);
        ItemStack lightgrey_dye = new ItemStack(Items.LIGHT_GRAY_DYE);
        ItemStack lime_dye = new ItemStack(Items.LIME_DYE);
        ItemStack magenta_dye = new ItemStack(Items.MAGENTA_DYE);
        ItemStack orange_dye = new ItemStack(Items.ORANGE_DYE);
        ItemStack pink_dye = new ItemStack(Items.PINK_DYE);
        ItemStack cyan_dye = new ItemStack(Items.CYAN_DYE);
        ItemStack gray_dye = new ItemStack(Items.GRAY_DYE);
        ItemStack purple_dye = new ItemStack(Items.PURPLE_DYE);
        ItemStack shears = new ItemStack(Items.SHEARS);
        ItemStack wax = new ItemStack(Items.HONEYCOMB);

        if (player.getStackInHand(hand).isItemEqual(black_dye) && pressingShift) {
            setColor("black");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);

            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(red_dye) && pressingShift) {
            setColor("red");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(blue_dye) && pressingShift) {
            setColor("blue");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(brown_dye) && pressingShift) {
            setColor("brown");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(green_dye) && pressingShift) {
            setColor("green");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(white_dye) && pressingShift) {
            setColor("white");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(yellow_dye) && pressingShift) {
            setColor("yellow");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(lightblue_dye) && pressingShift) {
            setColor("lightblue");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(lightgrey_dye) && pressingShift) {
            setColor("lightgrey");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(lime_dye) && pressingShift) {
            setColor("lime");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(magenta_dye) && pressingShift) {
            setColor("magenta");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(orange_dye) && pressingShift) {
            setColor("orange");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(pink_dye) && pressingShift) {
            setColor("pink");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(cyan_dye) && pressingShift) {
            setColor("cyan");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(gray_dye) && pressingShift) {
            setColor("gray");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(purple_dye) && pressingShift) {
            setColor("purple");
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(shears) && pressingShift) {
            setColor("ow");
            int damage = player.getStackInHand(hand).getDamage();
            player.getStackInHand(hand).setDamage(damage - 1);
            return super.interactMob(player, hand);
        }
        if (player.getStackInHand(hand).isItemEqual(wax) && pressingShift) {
            waterproof = true;
            int count = player.getStackInHand(hand).getCount();
            player.getStackInHand(hand).setCount(count-1);
            return super.interactMob(player, hand);
        }

        if (!this.hasPassengers() && !pressingShift) {
            player.startRiding(this);
            mount = true;
            if (isWaterDamaged()) {
//                if (world.isClient) {
//                    MinecraftClient.getInstance().setScreen(new WarningScreen());
//                }
            }
            needSpeed = 1.11f;
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                PacketByteBuf buf = PacketByteBufs.create();
                System.out.println("getbat"+getBattery());
                buf.writeFloat(getBattery());
                buf.writeBoolean(isWaterproof());
                if (isWaterDamaged()) {
                    buf.writeInt(1);
                }
                else {
                    buf.writeInt(0);
                }
                ServerPlayNetworking.send((ServerPlayerEntity) player, OneWheel.BATTERY , buf);
                OneWheelPlayerEntity ow = (OneWheelPlayerEntity) OneWheel.OWPE.spawnFromItemStack(serverWorld, player.getMainHandStack(), player, this.getBlockPos(), SpawnReason.EVENT, true, false);
                ow.setPosition(getControllingPassenger().getPos());
                ow.setSyncedplayer(getControllingPassenger().getUuidAsString());

                PacketByteBuf buf1 = PacketByteBufs.create();
                buf1.writeString(getControllingPassenger().getUuidAsString());
                buf1.writeInt(ow.getId());
                System.out.println("id is "+ow.getId());
                ServerPlayNetworking.send((ServerPlayerEntity) player, OneWheel.FAKE_PLAYER_PACKET, buf1);

                PlayerEntity assignedPlayer = (PlayerEntity) getControllingPassenger();
                setLeftHanded(assignedPlayer.getMainArm().equals(Arm.LEFT));
                ow.equipStack(EquipmentSlot.MAINHAND, assignedPlayer.getMainHandStack());
                ow.equipStack(EquipmentSlot.OFFHAND, assignedPlayer.getOffHandStack());
                ow.equipStack(EquipmentSlot.HEAD, assignedPlayer.getEquippedStack(EquipmentSlot.HEAD));
                ow.equipStack(EquipmentSlot.CHEST, assignedPlayer.getEquippedStack(EquipmentSlot.CHEST));
                ow.equipStack(EquipmentSlot.LEGS, assignedPlayer.getEquippedStack(EquipmentSlot.LEGS));
                ow.equipStack(EquipmentSlot.FEET, assignedPlayer.getEquippedStack(EquipmentSlot.FEET));
            }
            return super.interactMob(player, hand);
        }

        return super.interactMob(player, hand);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Override
    public void initDataTracker() {
        this.dataTracker.startTracking(nbtcolor, "ow");
        this.dataTracker.startTracking(nbtbattery, 32186.88f);
        this.dataTracker.startTracking(nbtwaterproof, false);
        this.dataTracker.startTracking(nbtwaterdamage, false);
        super.initDataTracker();
    }

    @Override
    public void travel(Vec3d pos) {
        breakingb = false;
        breakingf = false;

        if (this.isAlive()) {
            yawVelocity = 0.0F;
            if (forcedF > 30 || forcedb > 30 || f == 0) {
                if (this.hasPassengers() && (forcedF > 30 || forcedb > 30)) {
                    Entity livingEntity = this.getControllingPassenger();
                    livingEntity.dismountVehicle();
                    updatePassengerForDismount((LivingEntity) livingEntity);
                    livingEntity.setInvisible(false);
                }
                forcedb = 0;
                forcedF = 0;
                noseDivingf = false;
                noseDivingb = false;
            }
            if (this.hasPassengers()) {
                Entity livingEntity = this.getControllingPassenger();
                if (forcedb == 25 || forcedF == 25 && playerShouldDie(livingEntity)) {
                    livingEntity.damage(CrashDamageSource.CRASH, 20);
                }
                if (world.isClient() && this.getControllingPassenger().isPlayer()) {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player.getUuidAsString().equals(this.getControllingPassenger().getUuidAsString())) {
                        pressingForward = player.input.pressingForward && battery > 0;
                        pressingBack = player.input.pressingBack && battery > 0;
                        pressingLeft = player.input.pressingLeft && battery > 0;
                        pressingRight = player.input.pressingRight && battery > 0;
                        pressingCtrl = MinecraftClient.getInstance().options.sprintKey.isPressed();

                        PacketByteBuf buf = PacketByteBufs.create();

                        buf.writeBoolean(pressingForward);
                        buf.writeBoolean(pressingBack);
                        buf.writeBoolean(pressingLeft);
                        buf.writeBoolean(pressingRight);
                        buf.writeBoolean(pressingCtrl);

                        ClientPlayNetworking.send(InputPacket.PACKET_ID, buf);
                    }
                }
                ghost = false;
                LivingEntity livingentity = (LivingEntity) this.getControllingPassenger();
                if (pressingLeft) {
                    this.yawVelocity -= 1.5F;
                }

                if (pressingRight) {
                    this.yawVelocity += 1.5F;
                }

                if (pressingRight != pressingLeft && !pressingForward && !pressingBack) {
                    f += 0.005F;
                }
                if (!world.isClient) {
                    if (Math.abs(prevprevf) < Math.abs(f)) {
                        if (Math.abs(f) > 0.74074074074f && (!noseDivingb) && (!noseDivingf)) {
                            odds -= 1;

                            int x = rand.nextInt(odds);
                            if (x == 0) {
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeBoolean(true);
                                needSpeed = 0.1f;

                                ServerPlayNetworking.send((ServerPlayerEntity) livingentity , OneWheel.PACKET_ID , buf);
                            }
                        } else odds = 250;
                    }
                }
                if (f < needSpeed*-1 || noseDivingb) {
                    forcedb += 1;
                    noseDivingb = true;
                    if (forcedb > 20) {
                        pressingBack = false;
                        f /= 1.2;
                        fdecay /= 1.2;
                    }
                }
                if (f > needSpeed || noseDivingf) {
                    forcedF += 1;
                    noseDivingf = true;
                    if (forcedF > 20) {
                        f /= 1.2;
                        fdecay /= 1.2;
                        pressingForward = false;
                    }
                }

                prevprevf = f;
                if (pressingForward) {
                    if (f < 0) {
                        f /= 1.2;
                        fdecay /= 1.2;
                        if (prevF < f) {
                            breakingb = true;
                            breakingf = false;
                        }
                    }
                    if (pressingCtrl) {
                        fdecay += 0.000006;
                        f += 0.0039 - fdecay;
                    }
                    float mps = f * 0.082849355f;
                    float ratio = mps / 3.576f;
                    battery -= ratio(mps , ratio) / 20;
                }

                if (pressingBack) {
                    if (f > 0) {
                        f /= 1.2;
                        bdecay /= 1.2;
                        if (prevF > f) {
                            breakingf = true;
                            breakingb = false;
                        }
                    }
                    if (pressingCtrl) {
                        bdecay += 0.000006;
                        f -= 0.0039 - bdecay;
                    }
                    float mps = f * 0.082849355f;
                    float ratio = mps / 3.576f;
                    battery -= ratio(mps , ratio) / 20;
                }

                if (!pressingBack && !pressingForward && !noseDivingb && !noseDivingf) {
                    if (f > 0.008f) {
                        f/=1.2;
                        fdecay /=1.2;
                        if (prevF > f) {
                            System.out.println("breaking f");
                            breakingf = true;
                            breakingb = false;
                        }
                    }
                    if (f < -0.008f) {
                        f/=1.2;
                        bdecay /=1.2;
                        if (prevF < f) {
                            System.out.println("breaking b");
                            breakingb = true;
                            breakingf = false;
                        }
                    }
                    if(Math.abs(f) < 0.008f){
                        f = 0.0f;
                    }
                    if (pressingLeft) {
                        this.yawVelocity -= 4.5F;
                    }

                    if (pressingRight) {
                        this.yawVelocity += 4.5F;
                    }
                    if (f == 0) {
                        noseDivingf = false;
                        noseDivingb = false;
                        breakingb = false;
                        breakingf = false;
                        bdecay = 0;
                        fdecay = 0;
                    }
                }
                if (f >= 1.1111F) {
                    f = 1.1111F;
                    bdecay = 0f;
                    fdecay = 0f;
                }
                if (f <= -1.1111F) {
                    f = -1.1111F;
                    bdecay = 0f;
                    fdecay = 0f;
                }

                if (breakingb || breakingf) {
                    float recharge = ((f-prevprevf)*0.00082849355f);
                    if (f > 0) {
                        battery -= recharge;
                    } else if (f < 0) {
                        battery += recharge;
                    }
                }
                this.setMovementSpeed(1);

                HitResult deg15 = blockRaycast(45);
                if (deg15.getType().equals(HitResult.Type.BLOCK)) {
                    f = 0;
                }
                HitResult degneg15 = blockRaycast(-45);
                if (degneg15.getType().equals(HitResult.Type.BLOCK)) {
                    f *= 0.98;
                }
                HitResult deg0 = blockRaycast(0);
                if (deg0.getType().equals(HitResult.Type.BLOCK)) {
                    f *= 0.98;
                }
                Vec3d vec3d = new Vec3d(0, 0, (f*0.28f)*0.9785f);

                super.travel(vec3d);

                //String[] strings = this.get.toShortString().split(", ");
                //Vec3d npos = new Vec3d(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), Float.parseFloat(strings[2]));
            }
            else {
                //ghosting
//                if (time <= 10) {
//                    System.out.println("rip");
//                    breakingf = false;
//                    breakingb = false;
//                }
                this.setYaw(this.getYaw() + this.yawVelocity);
                this.setMovementSpeed(1F);
                this.bodyYaw = this.getYaw();
                this.headYaw = this.bodyYaw;
                Vec3d vec3d = new Vec3d(0 , 0 , (f*0.28f)*0.9785f);

                super.travel(vec3d);
//                if (!ghost) {
//                    System.out.println(breakingf);
//                    if ((breakingf || breakingb) && time > 10) {
//                        ghost = true;
//                    } else {
                f /= 1.6;
                fdecay /= 1.6;
                bdecay /= 1.6;
                if (Math.abs(f) < 0.008) {
                    f = 0;
                }
                if (f > 0) {
                    breakingb = true;
                }
                if (f < 0) {
                    breakingf = true;
                }
                if (f == 0) {
                    breakingb = false;
                    breakingf = false;
                }
                    //}
                //}
            }
            prevF = f;
            prevbd = bdecay;
            prevfd = fdecay;
        }
    }

    private boolean playerShouldDie(Entity player) {

        Vec3d vec3d = player.getPos();
        BlockHitResult result = world.raycast(new RaycastContext(vec3d, vec3d.subtract(0, 2, 0), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.WATER, this));
        boolean inWater = false;
        boolean offGround = true;
        if (result.getType().equals(HitResult.Type.BLOCK)) {
            if (world.getBlockState(result.getBlockPos()).getBlock().getName().toString().contains("water")) {
                inWater = true;
            }
            offGround = false;
        }
        if (inWater || offGround) {
            return false;
        }
        return true;
    }

    private float ratio(float v, float ratio) {
        if (ratio >= 1) return ratio*v;
        if (v == 0) return 0;
        return (v/ratio)*1.25f;
    }
    private BlockHitResult blockRaycast(float angle) {

        float degrees = 0;
        if (f < 0) {
            degrees = 180;
        }

        Vec3d vec3dx = this.getCameraPosVec(1);
        Vec3d vec3dx2 = this.getRotationVec(1).rotateY((float) Math.toRadians(degrees+angle));
        Vec3d vec3dx3 = vec3dx.add(vec3dx2.x*0.6, vec3dx2.y*0.65, vec3dx2.z*0.6);
        return world.raycast(new RaycastContext(vec3dx, vec3dx3, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, this));

    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getSource();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (player.getMainHandStack().isEmpty()) {
                ItemStack ow = new ItemStack(OneWheel.oneWheel);
                ow.setCount(1);
                ow.getOrCreateNbt().putString("color", getColor());
                ow.getOrCreateNbt().putFloat("battery", 32186.88f-getBattery());
                ow.getOrCreateNbt().putBoolean("waterproof", isWaterproof());
                ow.getOrCreateNbt().putBoolean("waterdamage", isWaterDamaged());
                player.setStackInHand(player.getActiveHand() , ow);
                this.discard();
                return false;
            }
        }
        return true;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (forcedF > 5 || forcedb > 5) {
            passenger.setPosition(bonePos);
        }
        if (this.hasPassenger(passenger) && (forcedF == 0 && forcedb == 0)) {
            float f = 0.0F;
            float g = (float)((this.isRemoved() ? 0.009999999776482582D : this.getMountedHeightOffset()) + passenger.getHeightOffset()+0.18f);
            if (this.getPassengerList().size() > 1) {
                int i = this.getPassengerList().indexOf(passenger);
                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (passenger instanceof AnimalEntity) {
                    f += 0.2F;
                }
            }

            Vec3d vec3d = (new Vec3d((double)f, 0.0D, 0.0D)).rotateY(-this.getYaw() * 0.017453292F - 1.5707964F);
            if (!(forcedb > 5) && !(forcedF > 5)) {
                passenger.setPosition(this.getX() + vec3d.x , this.getY() + (double) g , this.getZ() + vec3d.z);
            }
            passenger.setYaw(passenger.getYaw() + this.yawVelocity);
            passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
            this.setPlayerYaw(passenger);
            if (!this.getEntityWorld().isClient) {
                if (forcedb == 0 && forcedF == 0) {
                    this.setYaw(this.getYaw() + yawVelocity);
                    this.bodyYaw = this.getYaw();
                    this.headYaw = this.bodyYaw;
                }
            }
            else {
                if (forcedb == 0 && forcedF == 0) {
                    this.bodyYaw = this.getYaw();
                    this.headYaw = this.bodyYaw;
                }
            }
            if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1) {
                int j = passenger.getId() % 2 == 0 ? 90 : 270;
                passenger.setBodyYaw(this.getYaw());
                passenger.setHeadYaw(passenger.getHeadYaw() + (float)j);
            }

        }
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        passenger.setInvisible(false);
        if (forcedF > 0 || forcedb > 0) {
            return bonePos;
        }
        return this.getPos();
    }

    public void setPlayerYaw(Entity entity) {
        entity.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw()+90);
        float g = MathHelper.clamp(f, -90, 90F);
        entity.prevYaw += g - f;
        entity.setYaw(entity.getYaw() + g - f);
        entity.setHeadYaw(entity.getYaw());

    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("color", color);
        nbt.putFloat("battery", battery);
        nbt.putBoolean("waterproof", isWaterproof());
        nbt.putBoolean("waterdamage", isWaterDamaged());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setColor(nbt.getString("color"));
        setBattery(nbt.getFloat("battery"));
        setWaterProof(nbt.getBoolean("waterproof"));
        setWaterDamage(nbt.getBoolean("waterdamage"));
    }

    public void setColor(String color) {
        this.color = color;
        this.dataTracker.set(nbtcolor, color);
    }
    public void setBattery(float battery) {
        this.battery = battery;
        this.dataTracker.set(nbtbattery, battery);
    }
    public void setWaterProof(boolean yn) {
        this.waterproof = yn;
        this.dataTracker.set(nbtwaterproof, waterproof);
    }
    public void setWaterDamage(boolean yn) {
        this.waterdamage = yn;
        this.dataTracker.set(nbtwaterdamage, waterdamage);
    }
    public String getColor() {
        color = this.dataTracker.get(nbtcolor);
        if (color.equals("")) {
            return "ow";
        }
        return color;
    }
    public float getBattery() {
        return this.dataTracker.get(nbtbattery);
    }
    public boolean isWaterproof() {
        return this.dataTracker.get(nbtwaterproof);
    }
    public boolean isWaterDamaged() {
        return this.dataTracker.get(nbtwaterdamage);
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
        data.addAnimationController(new AnimationController<OneWheelEntity>(this, "controller", 0, this::predicate));
        data.addAnimationController(new AnimationController<OneWheelEntity>(this, "lean", 10, this::tilt));
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
