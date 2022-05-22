package net.neednot.onewheel.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.network.GeckoLibNetwork;

import java.util.List;

public class BatteryItem extends Item {
    public BatteryItem(Settings properties) {
        super(properties);
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float battery = stack.getOrCreateNbt().getFloat("battery");
        Integer percent = 0;
        Formatting color = Formatting.RED;
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
        tooltip.add( new TranslatableText("item.onewheel.batterytip").append(" "+ percent +"%").formatted(color));
    }
}
