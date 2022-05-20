package net.neednot.onewheel.ui;

import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.neednot.onewheel.OneWheel;

import java.util.HashMap;
import java.util.Map;

public class WorkBenchScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private Map<Integer, Item> map() {
        Map<Integer, Item> map = new HashMap<>();
        map.put(0, Items.STICK);
        map.put(1, Items.APPLE);
        map.put(2, Items.APPLE);
        map.put(3, Items.APPLE);
        map.put(4, Items.APPLE);
        map.put(5, Items.APPLE);
        map.put(6, Items.APPLE);
        map.put(7, Items.APPLE);
        map.put(8, Items.APPLE);
        map.put(9, OneWheel.oneWheel.asItem());
        return map;
    }
    Inventory output;

    //This constructor gets called on the client when the server wants it to open the screenHandler,
    //The client will call the other constructor with an empty Inventory and the screenHandler will automatically
    //sync this empty inventory with the inventory on the server.
    public WorkBenchScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(10));
    }

    //This constructor gets called from the BlockEntity on the server without calling the other constructor first, the server knows the inventory of the container
    //and can therefore directly provide it as an argument. This inventory will then be synced to the client.
    public WorkBenchScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(OneWheel.WORK_BENCH_SCREEN_HANDLER, syncId);
        checkSize(inventory, 10);
        this.inventory = inventory;
        //this.output = new CraftingResultInventory();

        //some inventories do custom logic when a player opens it.
        inventory.onOpen(playerInventory.player);

        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        this.addListener(new ScreenHandlerListener() {
            @Override
            public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
                if (!inventory.getStack(0).isEmpty()) {
                    output.setStack(9, new ItemStack(OneWheel.oneWheel.asItem()));
                }
            }

            @Override
            public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

            }
        });
        int i;
        int j;
        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 3; ++j) {
                final int index = j+i*3;
                this.addSlot(new Slot(inventory, j + i * 3, 30 + j * 18, 17 + i * 18) {
                    public boolean canInsert(ItemStack stack) {
                        return stack.isOf(map().get(index));
                    }
                });
            }
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.addSlot(new Slot(inventory, 9, 124, 35){
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(OneWheel.oneWheel.asItem());
            }
        });

    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}
