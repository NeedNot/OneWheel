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
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) context.getWorld();
            BlockPos pos = context.getBlockPos();
            OneWheel.OW.spawnFromItemStack(serverWorld, context.getPlayer().getStackInHand(context.getHand()), context.getPlayer(), pos, SpawnReason.SPAWN_EGG, true, false);
            context.getPlayer().getStackInHand(context.getHand()).setCount(0);
        }
        return ActionResult.SUCCESS;
    }
}

