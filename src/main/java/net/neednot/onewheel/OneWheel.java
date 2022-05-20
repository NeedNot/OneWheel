package net.neednot.onewheel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.TaskExecutor;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;
import net.neednot.onewheel.item.FenderItem;
import net.neednot.onewheel.item.ItemRegister;
import net.neednot.onewheel.item.OneWheelItem;
import net.neednot.onewheel.packet.*;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;


public class OneWheel implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("onewheel");
	public static Identifier PACKET_ID = new Identifier("onewheel", "player_fall_packet");
	public static Identifier BATTERY = new Identifier("onewheel", "onewheel_battery_packet");
	public static Identifier FAKE_PLAYER_PACKET = new Identifier("onewheel", "fake_player_spawn_packet");

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
	public static final FenderItem fender = new FenderItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(1));

	public Map<Entity, Integer> entity = new HashMap<>();
	public int ticks;

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(((server) -> {
			if (!entity.isEmpty()) {
				Iterator it = entity.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<Entity, Integer> entry = (Map.Entry<Entity, Integer>) it.next();
					Entity entity1 = entry.getKey();
					if (entity1 instanceof OneWheelEntity) entry.setValue(entry.getValue() + 1);
					if (entity1.hasPassengers()) {
						OneWheelEntity ow = (OneWheelEntity) entity1;
						ServerWorld world = (ServerWorld) entity1.world;
						ow.reloadPlayer(world, ow);
						it.remove();
					}
					if (entry.getValue() > 6) it.remove();
				}
			}
		}));
		ServerEntityEvents.ENTITY_LOAD.register(((entity1 , world) ->  {
			if (entity1 instanceof OneWheelEntity) {
				entity.put(entity1, 0);
			}
		}));
		NoseDivePosPacket.registerPacket();
		InputPacket.registerPacket();
		BoardAnimToServerPacket.registerPacket();
		PlayerAnimToServerPacket.registerPacket();
		FabricDefaultAttributeRegistry.register(OW, OneWheelEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("onewheel", "onewheel"), oneWheel);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "fender"), fender);
		FabricDefaultAttributeRegistry.register(OWPE, OneWheelPlayerEntity.createMobAttributes());
	}
}
