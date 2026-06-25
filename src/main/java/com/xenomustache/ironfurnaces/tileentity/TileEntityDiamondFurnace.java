package com.xenomustache.ironfurnaces.tileentity;

import com.xenomustache.ironfurnaces.ModConfig;
import com.xenomustache.ironfurnaces.blocks.BlockDiamondFurnace;
import com.xenomustache.ironfurnaces.blocks.IFBlocks;
import com.xenomustache.ironfurnaces.container.ContainerModFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class TileEntityDiamondFurnace
extends TileEntityLockable
implements ITickable,
ISidedInventory {
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
    private static final int[] SLOTS_SIDES = new int[]{1};
    public static boolean keepInventory;
    public Block furnace;
    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);
    private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime;
    private String furnaceCustomName;

    public TileEntityDiamondFurnace() {
        this.markDirty();
    }

    public static void registerFixesFurnace(DataFixer fixer) {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityDiamondFurnace.class, new String[]{"Items"}));
    }

    @SideOnly(value=Side.CLIENT)
    public static boolean isBurning(IInventory inventory) {
        return inventory.getField(0) > 0;
    }

    public static int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        int burnTime = ForgeEventFactory.getItemBurnTime(stack);
        if (burnTime >= 0) {
            return burnTime;
        }
        Item item = stack.getItem();
        if (item == Item.getItemFromBlock(Blocks.WOODEN_SLAB)) {
            return 150;
        }
        if (item == Item.getItemFromBlock(Blocks.WOOL)) {
            return 100;
        }
        if (item == Item.getItemFromBlock(Blocks.CARPET)) {
            return 67;
        }
        if (item == Item.getItemFromBlock(Blocks.LADDER)) {
            return 300;
        }
        if (item == Item.getItemFromBlock(Blocks.WOODEN_BUTTON)) {
            return 100;
        }
        if (Block.getBlockFromItem(item).getDefaultState().getMaterial() == Material.WOOD) {
            return 300;
        }
        if (item == Item.getItemFromBlock(Blocks.COAL_BLOCK)) {
            return 16000;
        }
        if (item instanceof ItemTool && "WOOD".equals(((ItemTool)item).getToolMaterialName())) {
            return 200;
        }
        if (item instanceof ItemSword && "WOOD".equals(((ItemSword)item).getToolMaterialName())) {
            return 200;
        }
        if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe)item).getMaterialName())) {
            return 200;
        }
        if (item == Items.STICK) {
            return 100;
        }
        if (item != Items.BOW && item != Items.FISHING_ROD) {
            if (item == Items.SIGN) {
                return 200;
            }
            if (item == Items.COAL) {
                return 1600;
            }
            if (item == Items.LAVA_BUCKET) {
                return 20000;
            }
            if (item != Item.getItemFromBlock(Blocks.SAPLING) && item != Items.BOWL) {
                if (item == Items.BLAZE_ROD) {
                    return 2400;
                }
                if (item instanceof ItemDoor && item != Items.IRON_DOOR) {
                    return 200;
                }
                return item instanceof ItemBoat ? 400 : 0;
            }
            return 100;
        }
        return 300;
    }

    public static boolean isItemFuel(ItemStack stack) {
        return TileEntityDiamondFurnace.getItemBurnTime(stack) > 0;
    }

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.furnaceItemStacks) {
            if (itemstack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.furnaceItemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.furnaceItemStacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.furnaceItemStacks, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = this.furnaceItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.furnaceItemStacks.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        if (index == 0 && !flag) {
            this.totalCookTime = this.getCookTime(stack);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.furnaceCustomName : "container.furnace_2";
    }

    @Override
    public boolean hasCustomName() {
        return this.furnaceCustomName != null && !this.furnaceCustomName.isEmpty();
    }

    public void setCustomInventoryName(String p_145951_1_) {
        this.furnaceCustomName = p_145951_1_;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
        this.furnaceBurnTime = compound.getInteger("BurnTime");
        this.cookTime = compound.getInteger("CookTime");
        this.totalCookTime = compound.getInteger("CookTimeTotal");
        this.currentItemBurnTime = TileEntityDiamondFurnace.getItemBurnTime(this.furnaceItemStacks.get(1));
        if (compound.hasKey("CustomName", 8)) {
            this.furnaceCustomName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("BurnTime", (short)this.furnaceBurnTime);
        compound.setInteger("CookTime", (short)this.cookTime);
        compound.setInteger("CookTimeTotal", (short)this.totalCookTime);
        ItemStackHelper.saveAllItems(compound, this.furnaceItemStacks);
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.furnaceCustomName);
        }
        return compound;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    @Override
    public void update() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        if (this.isBurning()) {
            --this.furnaceBurnTime;
        }
        if (!this.world.isRemote) {
            ItemStack itemstack = this.furnaceItemStacks.get(1);
            if (this.isBurning() || !itemstack.isEmpty() && !this.furnaceItemStacks.get(0).isEmpty()) {
                if (!this.isBurning() && this.canSmelt()) {
                    this.currentItemBurnTime = this.furnaceBurnTime = TileEntityDiamondFurnace.getItemBurnTime(itemstack);
                    if (this.isBurning()) {
                        flag1 = true;
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                ItemStack item1 = item.getContainerItem(itemstack);
                                this.furnaceItemStacks.set(1, item1);
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canSmelt()) {
                    ++this.cookTime;
                    if (this.cookTime == this.totalCookTime) {
                        this.cookTime = 0;
                        this.totalCookTime = this.getCookTime(this.furnaceItemStacks.get(0));
                        this.smeltItem();
                        flag1 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.totalCookTime);
            }
            if (flag != this.isBurning()) {
                IBlockState iblockstate = this.world.getBlockState(this.pos);
                TileEntity tileentity = this.world.getTileEntity(this.pos);
                keepInventory = true;
                if (flag1) {
                    this.world.setBlockState(this.pos, IFBlocks.diamondFurnaceActive.getDefaultState().withProperty(BlockDiamondFurnace.FACING, iblockstate.getValue(BlockDiamondFurnace.FACING)), 3);
                    this.world.setBlockState(this.pos, IFBlocks.diamondFurnaceActive.getDefaultState().withProperty(BlockDiamondFurnace.FACING, iblockstate.getValue(BlockDiamondFurnace.FACING)), 3);
                } else {
                    this.world.setBlockState(this.pos, IFBlocks.diamondFurnaceIdle.getDefaultState().withProperty(BlockDiamondFurnace.FACING, iblockstate.getValue(BlockDiamondFurnace.FACING)), 3);
                    this.world.setBlockState(this.pos, IFBlocks.diamondFurnaceIdle.getDefaultState().withProperty(BlockDiamondFurnace.FACING, iblockstate.getValue(BlockDiamondFurnace.FACING)), 3);
                }
                keepInventory = false;
                if (tileentity != null) {
                    tileentity.validate();
                    this.world.setTileEntity(this.pos, tileentity);
                }
            }
        }
        if (flag1) {
            this.markDirty();
        }
    }

    public int getCookTime(ItemStack stack) {
        return ModConfig.DiamondFurnaceCookTime;
    }

    private boolean canSmelt() {
        if (this.furnaceItemStacks.get(0).isEmpty()) {
            return false;
        }
        ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(this.furnaceItemStacks.get(0));
        if (itemstack.isEmpty()) {
            return false;
        }
        ItemStack itemstack1 = this.furnaceItemStacks.get(2);
        if (itemstack1.isEmpty()) {
            return true;
        }
        if (!itemstack1.isItemEqual(itemstack)) {
            return false;
        }
        if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
            return true;
        }
        return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack itemstack = this.furnaceItemStacks.get(0);
            ItemStack itemstack1 = FurnaceRecipes.instance().getSmeltingResult(itemstack);
            ItemStack itemstack2 = this.furnaceItemStacks.get(2);
            if (itemstack2.isEmpty()) {
                this.furnaceItemStacks.set(2, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }
            if (itemstack.getItem() == Item.getItemFromBlock(Blocks.SPONGE) && itemstack.getMetadata() == 1 && !this.furnaceItemStacks.get(1).isEmpty() && this.furnaceItemStacks.get(1).getItem() == Items.BUCKET) {
                this.furnaceItemStacks.set(1, new ItemStack(Items.WATER_BUCKET));
            }
            itemstack.shrink(1);
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        }
        return player.getDistanceSq((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        }
        if (index != 1) {
            return true;
        }
        ItemStack itemstack = this.furnaceItemStacks.get(1);
        return TileEntityDiamondFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return SLOTS_BOTTOM;
        }
        return side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        Item item;
        return direction != EnumFacing.DOWN || index != 1 || (item = stack.getItem()) == Items.WATER_BUCKET || item == Items.BUCKET;
    }

    @Override
    public String getGuiID() {
        return "minecraft:furnace";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerModFurnace(playerInventory, this);
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: {
                return this.furnaceBurnTime;
            }
            case 1: {
                return this.currentItemBurnTime;
            }
            case 2: {
                return this.cookTime;
            }
            case 3: {
                return this.totalCookTime;
            }
        }
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: {
                this.furnaceBurnTime = value;
                break;
            }
            case 1: {
                this.currentItemBurnTime = value;
                break;
            }
            case 2: {
                this.cookTime = value;
                break;
            }
            case 3: {
                this.totalCookTime = value;
            }
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @Override
    public void clear() {
        this.furnaceItemStacks.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN) {
                return (T) this.handlerBottom;
            }
            if (facing == EnumFacing.UP) {
                return (T) this.handlerTop;
            }
            return (T) this.handlerSide;
        }
        return super.getCapability(capability, facing);
    }
}

