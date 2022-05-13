package net.neednot.onewheel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;
import net.neednot.onewheel.item.ItemRegister;
import net.neednot.onewheel.item.OneWheelItem;
import net.neednot.onewheel.packet.FallPacket;
import net.neednot.onewheel.packet.InputPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OneWheel implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("onewheel");
	public static Identifier PACKET_ID = new Identifier("onewheel", "player_fall_packet");
	public static Identifier BATTERY = new Identifier("onewheel", "onewheel_battery_packet");

	public static final EntityType<OneWheelEntity> OW = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("onewheel", "onewheel"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OneWheelEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.55f)).build()
	);
	public static final EntityType<OneWheelPlayerEntity> OWPE = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("onewheel", "onewheelplayer"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, OneWheelPlayerEntity::new).dimensions(EntityDimensions.fixed(0.75f, 0.55f)).build()
	);

	public static final OneWheelItem oneWheel = new OneWheelItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(1));

	@Override
	public void onInitialize() {
		InputPacket.registerPacket();
		FabricDefaultAttributeRegistry.register(OW, OneWheelEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("onewheel", "onewheel"), oneWheel);
		FabricDefaultAttributeRegistry.register(OWPE, OneWheelPlayerEntity.createMobAttributes());
	}
}
