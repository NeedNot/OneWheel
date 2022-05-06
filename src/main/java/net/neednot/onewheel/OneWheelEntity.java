package net.neednot.onewheel;

import ca.weblite.objc.Client;
import com.eliotlash.mclib.math.functions.limit.Min;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributes;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.entity.BikeEntity;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import java.time.LocalTime;
import java.util.Random;


public class OneWheelEntity extends AnimalEntity implements IAnimatable {

    public boolean pressingLeft;
    public boolean pressingRight;
    public boolean pressingForward;
    public boolean pressingBack;
    public boolean pressingShift;
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
    public float battery = 32186.88f;
    public int odds = 350;
    public float prevprevf;

    private static final TrackedData<String> nbtdata = DataTracker.registerData(OneWheelEntity.class, TrackedDataHandlerRegistry.STRING);

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
    public Random rand = getRand();

    public Random getRand() {
        Random random = new Random();
        if (world.isClient) random.setSeed(Long.parseLong(String.valueOf(this.getId())));
        if (!world.isClient) random.setSeed(Long.parseLong(String.valueOf(this.getId()+1)));
        return random;
    }

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
            return PlayState.CONTINUE;
        }
        if (f < -0.66f) {
            event.getController().setAnimation(pushbackb);
            return PlayState.CONTINUE;
        }

        if (breakingf) {
            event.getController().setAnimation(holdf);
            return PlayState.CONTINUE;
        }
        if (breakingb) {
            event.getController().setAnimation(holdb);
            return PlayState.CONTINUE;
        }

        if (f < 0.66f && f > 0) {
            event.getController().setAnimation(tiltf);
            return PlayState.CONTINUE;
        }

        if (f > -0.66f && f < 0) {
            event.getController().setAnimation(tiltb);
            return PlayState.CONTINUE;
        }

        if (!this.hasPassengers()) {
            event.getController().setAnimation(dismount);
            return PlayState.CONTINUE;
        }
        if (mount) {
            event.getController().setAnimation(mounta);
            mount = false;
            return PlayState.CONTINUE;
        }
        if (f == 0 && !name.contains("mount")) {
            event.getController().setAnimation(flat);
            return PlayState.CONTINUE;
        }


        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();
        if (forcedb == 0 && forcedF == 0) bonePos = this.getPos();
        this.setHealth((battery/32186.88f)*20);
        BlockPos pos = this.getBlockPos();
        if (world.getReceivedRedstonePower(pos) > 0) {
            battery += 0.0022352f;
        }
    }

    public OneWheelEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
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

        if (!this.hasPassengers() && !pressingShift) {
            player.startRiding(this);
            mount = true;
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                OneWheel.OWPE.spawnFromItemStack(serverWorld, player.getMainHandStack(), player, this.getBlockPos(), SpawnReason.EVENT, true, false);
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
        this.dataTracker.startTracking(nbtdata, "ow");
        super.initDataTracker();
    }

    @Override
    public void travel(Vec3d pos) {
        //ghosting
//        if (breakingb || breakingf) {
//            time += 1;
//            System.out.println(time);
//        }
//        else {
//            time = 0;
//        }
//        if (f == 0) {
//            breakingb = false;
//            breakingf = false;
//        }
            breakingb = false;
            breakingf = false;
        try {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            pressingForward = player.input.pressingForward;
            pressingBack = player.input.pressingBack;
            pressingLeft = player.input.pressingLeft;
            pressingRight = player.input.pressingRight;
            pressingShift = player.isSneaking();
        } catch (Exception e) { }

        if (this.isAlive()) {
            yawVelocity = 0.0F;
            if (forcedb == 25 || forcedF == 25) {
                Entity player = this.getControllingPassenger();
                player.damage(DamageSource.FALL, 5);
            }
            if (forcedF > 30 || forcedb > 30 || f == 0) {
                if (this.hasPassengers() && (forcedF > 30 || forcedb > 30)) {
                    PlayerEntity player = (PlayerEntity) this.getControllingPassenger();
                    player.dismountVehicle();
                    updatePassengerForDismount((LivingEntity) player);
                    player.setInvisible(false);
                }
                forcedb = 0;
                forcedF = 0;
                noseDivingf = false;
                noseDivingb = false;
            }
            if (this.hasPassengers()) {
                PlayerEntity player = (PlayerEntity) getControllingPassenger();
                if (!world.isClient) {
                    player.sendMessage(new LiteralText(String.format("%.2f", Math.abs(f*27)) +" MPH") , true);
                }
//                if (forcedF > 20 || forcedb > 20) {
//                    this.getControllingPassenger().dismountVehicle();
//                }
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
                float needSpeed = 1.11f;
                if (Math.abs(f) > 0.74074074074f && Math.abs(prevprevf) < Math.abs(f) && (!noseDivingb) && (!noseDivingf)) {
                    odds -= 1;

                    int x = rand.nextInt(odds);
                    System.out.println(x);
                    if (x == 0) {
                        needSpeed = 0.1f;
                    }
                }
                else odds = 250;
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
                    fdecay += 0.000006;
                    f += 0.0039 - fdecay;
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
                    bdecay += 0.000006;
                    f -= 0.0039 - bdecay;
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
                    float recharge = ((f-prevF)*0.00082849355f)/20;
                    if (f > 0) {
                        battery -= recharge;
                    } else if (f < 0) {
                        battery += recharge;
                    }
                }
                this.setMovementSpeed(1F);
//                if (!MinecraftClient.getInstance().options.sprintKey.isPressed()) {
//                    f = prevF;
//                    bdecay = prevbd;
//                    fdecay = prevfd;
//                }

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

    private float ratio(float v, float ratio) {
        if (ratio >= 1) {
            return ratio*v;
        }
        return (v/ratio)*1.25f;
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        Entity entity = source.getSource();
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            ItemStack ow = new ItemStack(OneWheel.oneWheel);
            ow.setCount(1);
            player.setStackInHand(player.getActiveHand(), ow);
            this.discard();
        }
        return false;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
        if (forcedF > 5 || forcedb > 5) {
            passenger.setPosition(bonePos);
        }
        if (this.hasPassenger(passenger) && (forcedF == 0 && forcedb == 0)) {
            float f = 0.0F;
            float g = (float)((this.isRemoved() ? 0.009999999776482582D : this.getMountedHeightOffset()) + passenger.getHeightOffset()+0.15f);
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
        if (passenger instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity) passenger;
        }
        passenger.setInvisible(false);
//        double d = 3;
//        float f = -MathHelper.sin(this.getYaw() * 0.017453292F);
//        float g = MathHelper.cos(this.getYaw() * 0.017453292F);
//        float h = Math.max(Math.abs(f), Math.abs(g));
//        Vec3d vec3d = new Vec3d(this.getX(), this.getY(), this.getZ()).add(new Vec3d((double)f * d / (double)h, 0.0d, (double)g * d / (double)h));
//
//        d = -1;
//        f = -MathHelper.sin(this.getYaw()+90 * 0.017453292F);
//        g = MathHelper.cos(this.getYaw()+90 * 0.017453292F);
//        h = Math.max(Math.abs(f), Math.abs(g));
//        return vec3d.add(new Vec3d((double)f * d / (double)h, 0.0D, (double)g * d / (double)h));
        //return new Vec3d(this.getX()+x, this.getBoundingBox().maxY, this.getZ()+z);
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
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setColor(nbt.getString("color"));
    }

    public void setColor(String color) {
        this.color = color;
        this.dataTracker.set(nbtdata, color);
    }

    public String getColor() {
        color = this.dataTracker.get(nbtdata);
        if (color.equals("")) {
            return "ow";
        }
        return color;
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
