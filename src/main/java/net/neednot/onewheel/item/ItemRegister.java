package net.neednot.onewheel.item;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.OneWheel;

public class ItemRegister {
    public static void registerOneWheelItem() {
        FabricModelPredicateProviderRegistry.register(OneWheel.oneWheel, new Identifier("decals"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (getColor(itemStack) == 0) {
                return 0.0F;
            }
            return getColor(itemStack);
        });
    }

    private static float getColor(ItemStack itemStack) {
        String color;
        try {
            color = itemStack.getNbt().getString("color");
        } catch (NullPointerException e) {
            return 0.0f;
        }
        switch (color) {
            case "ow": return 0.0f;
            case "black": return 0.1f;
            case "brown": return 0.2f;
            case "cyan": return 0.3f;
            case "gray": return 0.4f;
            case "green": return 0.45f;
            case "lightblue": return 0.5f;
            case "lightgrey": return 0.55f;
            case "lime": return 0.6f;
            case "magenta": return 0.65f;
            case "orange": return 0.7f;
            case "pink": return 0.75f;
            case "purple": return 0.8f;
            case "red": return 0.85f;
            case "white": return 0.90f;
            case "yellow": return 0.95f;
            case "blue": return 1f;
        }
        return 0f;
    }
}
