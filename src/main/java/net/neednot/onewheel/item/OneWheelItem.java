package net.neednot.onewheel.item;


import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.neednot.onewheel.OneWheel;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import org.jetbrains.annotations.Nullable;

import java.text.Normalizer;
import java.util.List;

public class OneWheelItem extends Item {

    public OneWheelItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // default white text
        Formatting color = Formatting.RED;
        if (stack.getOrCreateNbt().getBoolean("waterproof")) {
            tooltip.add(new TranslatableText("item.onewheel.waterproofed").formatted(Formatting.AQUA));
        }
        if (stack.getOrCreateNbt().getString("color") != null) {
            switch (stack.getOrCreateNbt().getString("color")) {
                case "":
                case "ow":
                    color = Formatting.BOLD;
                    break;
                case "black":
                case "gray":
                    color = Formatting.DARK_GRAY;
                    break;
                case "blue":
                    color = Formatting.DARK_BLUE;
                    break;
                case "brown":
                case "orange":
                    color = Formatting.GOLD;
                    break;
                case "cyan":
                    color = Formatting.AQUA;
                    break;
                case "green":
                    color = Formatting.DARK_GREEN;
                    break;
                case "lightblue":
                    color = Formatting.BLUE;
                    break;
                case "lightgrey":
                    color = Formatting.GRAY;
                    break;
                case "lime":
                    color = Formatting.GREEN;
                    break;
                case "magenta":
                case "pink":
                case "purple":
                    color = Formatting.LIGHT_PURPLE;
                    break;
                case "red":
                    color = Formatting.RED;
                    break;
                case "white":
                    color = Formatting.WHITE;
                    break;
                case "yellow":
                    color = Formatting.YELLOW;
                    break;
            }
            if (color != Formatting.BOLD) {
                System.out.println(color);
                tooltip.add(new TranslatableText("item.onewheel.color").append(" "+stack.getOrCreateNbt().getString("color")).formatted(color));
            }
        }
        if (stack.getOrCreateNbt().getBoolean("fender")) {
            tooltip.add(new TranslatableText("item.onewheel.hasfender").formatted(Formatting.GREEN));
        }
        if (stack.getOrCreateNbt().getBoolean("waterdamage")) {
            tooltip.add(new TranslatableText("item.onewheel.damaged").formatted(Formatting.RED));
        }
        float battery = stack.getOrCreateNbt().getFloat("battery");
        Integer percent = 0;
        color = Formatting.RED;
        if (battery < 32186.88f) {
            percent = Integer.valueOf(String.format("%.0f" , ((32186.88f - battery) / 32186.88f) * 100));
        }
        if (percent > 33) {
            color = Formatting.YELLOW;
        }
        if (percent > 66) {
            color = Formatting.GREEN;
        }
        // formatted red text
        tooltip.add( new TranslatableText("item.onewheel.battery").append(" "+String.valueOf(percent)+"%").formatted(color));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) context.getWorld();
            BlockPos pos = context.getBlockPos();
            OneWheelEntity ow = (OneWheelEntity) OneWheel.OW.spawnFromItemStack(serverWorld, context.getPlayer().getStackInHand(context.getHand()), context.getPlayer(), pos, SpawnReason.SPAWN_EGG, true, false);
            ow.setColor(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getString("color"));
            ow.setBattery(32186.88f-(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getFloat("battery")));
            ow.setWaterProof(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getBoolean("waterproof"));
            ow.setWaterDamage(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getBoolean("waterdamage"));
            ow.setFender(context.getPlayer().getStackInHand(context.getHand()).getOrCreateNbt().getBoolean("fender"));
            context.getPlayer().getStackInHand(context.getHand()).setCount(0);
        }
        return ActionResult.SUCCESS;
    }
}

