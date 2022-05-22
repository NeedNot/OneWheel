package net.neednot.onewheel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.thread.TaskExecutor;
import net.neednot.onewheel.block.WorkBench;
import net.neednot.onewheel.block.WorkBenchEntity;
import net.neednot.onewheel.entity.board.OneWheelEntity;
import net.neednot.onewheel.entity.player.OneWheelPlayerEntity;
import net.neednot.onewheel.item.*;
import net.neednot.onewheel.packet.*;
import net.neednot.onewheel.ui.WorkBenchScreenHandler;
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

	public static final Block WORKBENCH = new WorkBench(FabricBlockSettings.of(Material.METAL).strength(4.0f));

	public static final Identifier BENCH = new Identifier("onewheel", "workbench");

	public static BlockEntityType<WorkBenchEntity> WORK_BENCH_ENTITY;

	public static Identifier PACKET_ID = new Identifier("onewheel", "player_fall_packet");
	public static Identifier BATTERY = new Identifier("onewheel", "onewheel_battery_packet");
	public static Identifier FAKE_PLAYER_PACKET = new Identifier("onewheel", "fake_player_spawn_packet");

	public static ScreenHandlerType<WorkBenchScreenHandler> WORK_BENCH_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(BENCH, WorkBenchScreenHandler::new);

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
	public static final BatteryItem battery = new BatteryItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(64));
	public static final DeckItem deck = new DeckItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(1));
	public static final WheelItem wheel = new WheelItem(new Item.Settings().group(ItemGroup.TRANSPORTATION).maxCount(1));

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
					if (entry != null) {
						if (entry.getValue() > 6) it.remove();
					}
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
		WORK_BENCH_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, BENCH, FabricBlockEntityTypeBuilder.create(WorkBenchEntity::new, WORKBENCH).build(null));
		Registry.register(Registry.BLOCK, new Identifier("onewheel", "workbench"), WORKBENCH);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "workbench"), new BlockItem(WORKBENCH, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		FabricDefaultAttributeRegistry.register(OW, OneWheelEntity.createMobAttributes());
		Registry.register(Registry.ITEM, new Identifier("onewheel", "onewheel"), oneWheel);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "fender"), fender);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "battery"), battery);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "deck"), deck);
		Registry.register(Registry.ITEM, new Identifier("onewheel", "wheel"), wheel);
		FabricDefaultAttributeRegistry.register(OWPE, OneWheelPlayerEntity.createMobAttributes());
	}
}
