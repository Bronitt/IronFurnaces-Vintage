package com.xenomustache.ironfurnaces.container;

import com.xenomustache.ironfurnaces.tileentity.TileEntityDiamondFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGlassFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGoldFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityIronFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityObsidianFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

public class ContainerModFurnace
extends Container {
    private final IInventory tileFurnace;
    private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;

    public ContainerModFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory) {
        this.tileFurnace = furnaceInventory;
        this.addSlotToContainer(new Slot(furnaceInventory, 0, 56, 17));
        this.addSlotToContainer(new SlotFurnaceFuel(furnaceInventory, 1, 56, 53));
        this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, 2, 116, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void addListener(@NotNull IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileFurnace);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener icontainerlistener : this.listeners) {
            if (this.cookTime != this.tileFurnace.getField(2)) {
                icontainerlistener.sendWindowProperty(this, 2, this.tileFurnace.getField(2));
            }
            if (this.furnaceBurnTime != this.tileFurnace.getField(0)) {
                icontainerlistener.sendWindowProperty(this, 0, this.tileFurnace.getField(0));
            }
            if (this.currentItemBurnTime != this.tileFurnace.getField(1)) {
                icontainerlistener.sendWindowProperty(this, 1, this.tileFurnace.getField(1));
            }
            if (this.totalCookTime == this.tileFurnace.getField(3)) continue;
            icontainerlistener.sendWindowProperty(this, 3, this.tileFurnace.getField(3));
        }
        this.cookTime = this.tileFurnace.getField(2);
        this.furnaceBurnTime = this.tileFurnace.getField(0);
        this.currentItemBurnTime = this.tileFurnace.getField(1);
        this.totalCookTime = this.tileFurnace.getField(3);
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        this.tileFurnace.setField(id, data);
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer playerIn) {
        return this.tileFurnace.isUsableByPlayer(playerIn);
    }

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0 ? (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty() ? !this.mergeItemStack(itemstack1, 0, 1, false) : (TileEntityDiamondFurnace.isItemFuel(itemstack1) || TileEntityGoldFurnace.isItemFuel(itemstack1) || TileEntityGlassFurnace.isItemFuel(itemstack1) || TileEntityIronFurnace.isItemFuel(itemstack1) || TileEntityObsidianFurnace.isItemFuel(itemstack1) ? !this.mergeItemStack(itemstack1, 1, 2, false) : (index >= 3 && index < 30 ? !this.mergeItemStack(itemstack1, 30, 39, false) : index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)))) : !this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}

