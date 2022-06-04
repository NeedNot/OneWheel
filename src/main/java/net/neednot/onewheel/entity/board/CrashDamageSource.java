package net.neednot.onewheel.entity.board;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class CrashDamageSource extends DamageSource {
    public static final CrashDamageSource CRASH = new CrashDamageSource("crash");
    @Override
    public Text getDeathMessage(LivingEntity entity) {
        return new TranslatableText("death.attack.crash", entity.getName());
    }
    protected CrashDamageSource(String name) {
        super(name);
    }

}
