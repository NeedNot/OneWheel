package net.neednot.onewheel.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeckItem extends Item {
    public DeckItem(Settings properties) {
        super(properties);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getOrCreateNbt().getBoolean("waterproof")) {
            tooltip.add(new TranslatableText("item.onewheel.waterproofed").formatted(Formatting.AQUA));
        }
        Formatting color = Formatting.BOLD;
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
    }

}
