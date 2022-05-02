package net.neednot.onewheel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.example.registry.EntityRegistry;


public class OneWheel implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("onewheel");

	public static final EntityType<OneWheelEntity> OW = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("future_motion", "onewheel"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OneWheelEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.55f)).build()
	);
	public static final EntityType<OneWheelPlayerEntity> OWPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("future_motion", "onewheelplayer"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OneWheelPlayerEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.55f)).build()
	);

	public static final OneWheelItem oneWheel = new OneWheelItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(1));

	@Override
	public void onInitialize() {
		EntityRendererRegistry.register(OW, OneWheelRender::new);
		FabricDefaultAttributeRegistry.register(OW, OneWheelEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("modid", "onewheel"), oneWheel);
		EntityRendererRegistry.register(OWPE, OneWheelPlayerRender::new);
		FabricDefaultAttributeRegistry.register(OWPE, OneWheelPlayerEntity.createMobAttributes());
	}
}
