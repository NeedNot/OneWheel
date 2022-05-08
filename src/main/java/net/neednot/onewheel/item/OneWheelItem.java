package net.neednot.onewheel.item;


import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;

public class OneWheelItem extends Item {

    public OneWheelItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) context.getWorld();
            BlockPos pos = context.getBlockPos();
            OneWheelEntity ow = (OneWheelEntity) OneWheel.OW.spawnFromItemStack(serverWorld, context.getPlayer().getStackInHand(context.getHand()), context.getPlayer(), pos, SpawnReason.SPAWN_EGG, true, false);
            ow.setColor(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getString("color"));
            context.getPlayer().getStackInHand(context.getHand()).setCount(0);
        }
        return ActionResult.SUCCESS;
    }
}

