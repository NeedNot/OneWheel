package net.neednot.onewheel.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Objects;

public class WarningScreen extends Screen {

    private static final Identifier DEMO_BG = new Identifier("textures/gui/demo_background.png");
    private MultilineText movementText;
    private MultilineText fullWrappedText;
    private MultilineText fullWrappedText2;


    public WarningScreen() {
        super(new TranslatableText("onewheel.warning.water.title"));
        this.movementText = MultilineText.EMPTY;
        this.fullWrappedText = MultilineText.EMPTY;
        this.fullWrappedText2 = MultilineText.EMPTY;
    }
    @Override
    protected void init() {
        this.addDrawableChild(new ButtonWidget(this.width/2-57, this.height / 2 + 62 + -16, 114, 20, new TranslatableText("onewheel.ok"), (button) -> {
            this.client.setScreen((Screen)null);
            this.client.mouse.lockCursor();
        }));
        this.movementText = MultilineText.create(this.textRenderer,new TranslatableText("onewheel.warning.water.problem"), new TranslatableText("onewheel.warning.water.reason"));
        this.fullWrappedText = MultilineText.create(this.textRenderer, new TranslatableText("onewheel.warning.water.fix"), 218);
        this.fullWrappedText2 = MultilineText.create(this.textRenderer, new TranslatableText("onewheel.warning.water.warranty"), 218);
    }
    @Override
    public void renderBackground(MatrixStack matrices) {
        super.renderBackground(matrices);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DEMO_BG);
        int i = (this.width - 248) / 2;
        int j = (this.height - 166) / 2;
        this.drawTexture(matrices, i, j, 0, 0, 248, 166);
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        int i = (this.width - 248) / 2 + 10;
        int j = (this.height - 166) / 2 + 8;
        this.textRenderer.draw(matrices, this.title, (float)i, (float)j, 2039583);
        j = this.movementText.draw(matrices, i, j + 12, 12, 5197647);
        MultilineText var10000 = this.fullWrappedText;
        MultilineText var100002 = this.fullWrappedText2;
        int var10003 = j + 40;
        Objects.requireNonNull(this.textRenderer);
        var10000.draw(matrices, i, j, 9, 5197647);
        var100002.draw(matrices, i, var10003, 9, 2039583);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
