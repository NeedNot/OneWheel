package net.neednot.onewheel;


import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neednot.onewheel.OneWheelEntity;

public class OneWheelItem extends Item {

    public OneWheelItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        System.out.println("rn");
        if (!world.isClient) {
            System.out.println("in");
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos pos = playerEntity.getCameraBlockPos();
            OneWheel.OW.spawnFromItemStack(serverWorld, playerEntity.getStackInHand(hand), playerEntity, pos, SpawnReason.SPAWN_EGG, true, false);
            playerEntity.getStackInHand(hand).setCount(0);
        }
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }
}

