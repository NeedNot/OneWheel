package net.neednot.onewheel.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.text.TranslatableText;
import net.neednot.onewheel.ui.WarningScreen;

public class ScreenSetter {
    public static void setWarningScreen() {
        MinecraftClient.getInstance().setScreen(new WarningScreen());
    }
}
