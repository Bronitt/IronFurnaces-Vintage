/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.EnumPushReaction
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.texture.ITickable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MoverType
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.inventory.SlotFurnaceFuel
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBoat
 *  net.minecraft.item.ItemDoor
 *  net.minecraft.item.ItemHoe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.play.server.SPacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityLockableLoot
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$AxisDirection
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.datafix.DataFixer
 *  net.minecraft.util.datafix.FixTypes
 *  net.minecraft.util.datafix.IDataWalker
 *  net.minecraft.util.datafix.walkers.ItemStackDataLists
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.event.ForgeEventFactory
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.wrapper.SidedInvWrapper
 */
package com.xenomustache.ironfurnaces.tileentity;

import com.xenomustache.ironfurnaces.blocks.BlockShulkerFurnace;
import com.xenomustache.ironfurnaces.container.ContainerModFurnace;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackDataLists;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class TileEntityShulkerFurnace
extends TileEntityLockableLoot
implements ITickable,
ISidedInventory {
    private NonNullList<ItemStack> furnaceItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    private boolean hasBeenCleared;
    private int openCount;
    private AnimationStatus animationStatus;
    private float progress;
    private float progressOld;
    private EnumDyeColor color;
    private boolean destroyedByCreativePlayer;
    private static final int[] SLOTS_TOP = new int[]{0};
    private static final int[] SLOTS_BOTTOM = new int[]{2, 1};
    private static final int[] SLOTS_SIDES = new int[]{1};
    public static boolean keepInventory;
    public Block furnace;
    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);
    private int furnaceBurnTime;
    private int currentItemBurnTime;
    private int cookTime;
    private int totalCookTime;
    private String furnaceCustomName;

    public TileEntityShulkerFurnace() {
        this(null);
    }

    public TileEntityShulkerFurnace(@Nullable EnumDyeColor colorIn) {
        this.markDirty();
        this.furnaceItemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
        this.animationStatus = AnimationStatus.CLOSED;
        this.color = colorIn;
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
        return TileEntityShulkerFurnace.getItemBurnTime(stack) > 0;
    }

    public void update() {
        boolean flag = this.isBurning();
        boolean flag1 = false;
        this.updateAnimation();
        if (this.animationStatus == AnimationStatus.OPENING || this.animationStatus == AnimationStatus.CLOSING) {
            this.moveCollidedEntities();
        }
        if (this.isBurning()) {
            --this.furnaceBurnTime;
        }
        if (!this.world.isRemote) {
            ItemStack itemstack = this.furnaceItemStacks.get(1);
            if (this.isBurning() || !itemstack.isEmpty() && !this.furnaceItemStacks.get(0).isEmpty()) {
                if (!this.isBurning() && this.canSmelt()) {
                    this.currentItemBurnTime = this.furnaceBurnTime = TileEntityShulkerFurnace.getItemBurnTime(itemstack);
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
        return 200;
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 2) {
            return false;
        }
        if (index != 1) {
            return true;
        }
        ItemStack itemstack = this.furnaceItemStacks.get(1);
        return TileEntityShulkerFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.getItem() != Items.BUCKET;
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
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        if (stack == null) return;
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

    protected void updateAnimation() {
        this.progressOld = this.progress;
        switch (this.animationStatus) {
            case CLOSED: {
                this.progress = 0.0f;
                break;
            }
            case OPENING: {
                this.progress += 0.1f;
                if (!(this.progress >= 1.0f)) break;
                this.moveCollidedEntities();
                this.animationStatus = AnimationStatus.OPENED;
                this.progress = 1.0f;
                break;
            }
            case CLOSING: {
                this.progress -= 0.1f;
                if (!(this.progress <= 0.0f)) break;
                this.animationStatus = AnimationStatus.CLOSED;
                this.progress = 0.0f;
                break;
            }
            case OPENED: {
                this.progress = 1.0f;
            }
        }
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    public AnimationStatus getAnimationStatus() {
        return this.animationStatus;
    }

    public AxisAlignedBB getBoundingBox(IBlockState p_190584_1_) {
        return this.getBoundingBox(p_190584_1_.getValue(BlockShulkerFurnace.FACING));
    }

    public AxisAlignedBB getBoundingBox(EnumFacing p_190587_1_) {
        return Block.FULL_BLOCK_AABB.expand(0.5f * this.getProgress(1.0f) * (float)p_190587_1_.getFrontOffsetX(), 0.5f * this.getProgress(1.0f) * (float)p_190587_1_.getFrontOffsetY(), 0.5f * this.getProgress(1.0f) * (float)p_190587_1_.getFrontOffsetZ());
    }

    private AxisAlignedBB getTopBoundingBox(EnumFacing p_190588_1_) {
        EnumFacing enumfacing = p_190588_1_.getOpposite();
        return this.getBoundingBox(p_190588_1_).contract(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY(), enumfacing.getFrontOffsetZ());
    }

    private void moveCollidedEntities() {
        EnumFacing enumfacing;
        AxisAlignedBB axisalignedbb;
        List<Entity> list;
        IBlockState iblockstate = this.world.getBlockState(this.getPos());
        if (iblockstate.getBlock() instanceof BlockShulkerFurnace && !(list = this.world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb = this.getTopBoundingBox(enumfacing = iblockstate.getValue(BlockShulkerFurnace.FACING)).offset(this.pos))).isEmpty()) {
            for (Entity entity : list) {
                if (entity.getPushReaction() == EnumPushReaction.IGNORE) continue;
                double d0 = 0.0;
                double d1 = 0.0;
                double d2 = 0.0;
                AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();
                switch (enumfacing.getAxis()) {
                    case X: {
                        d0 = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? axisalignedbb.maxX - axisalignedbb1.minX : axisalignedbb1.maxX - axisalignedbb.minX;
                        d0 += 0.01;
                        break;
                    }
                    case Y: {
                        d1 = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? axisalignedbb.maxY - axisalignedbb1.minY : axisalignedbb1.maxY - axisalignedbb.minY;
                        d1 += 0.01;
                        break;
                    }
                    case Z: {
                        d2 = enumfacing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? axisalignedbb.maxZ - axisalignedbb1.minZ : axisalignedbb1.maxZ - axisalignedbb.minZ;
                        d2 += 0.01;
                    }
                }
                entity.move(MoverType.SHULKER_BOX, d0 * (double) enumfacing.getFrontOffsetX(), d1 * (double) enumfacing.getFrontOffsetY(), d2 * (double) enumfacing.getFrontOffsetZ());
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return this.furnaceItemStacks.size();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.openCount = type;
            if (type == 0) {
                this.animationStatus = AnimationStatus.CLOSING;
            }
            if (type == 1) {
                this.animationStatus = AnimationStatus.OPENING;
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++this.openCount;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);
            if (this.openCount == 1) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, this.world.rand.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator()) {
            --this.openCount;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.openCount);
            if (this.openCount <= 0) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5f, this.world.rand.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerModFurnace(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return "minecraft:furnace";
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

    @Override
    public String getName() {
        return this.hasCustomName() ? this.furnaceCustomName : "container.shulker_furnace_-1";
    }

    public void setCustomInventoryName(String name) {
        this.furnaceCustomName = name;
    }

    public static void registerFixesShulkerFurnace(DataFixer fixer) {
        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityShulkerFurnace.class, new String[]{"Items"}));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
        this.furnaceBurnTime = compound.getInteger("BurnTime");
        this.cookTime = compound.getInteger("CookTime");
        this.totalCookTime = compound.getInteger("CookTimeTotal");
        this.currentItemBurnTime = TileEntityShulkerFurnace.getItemBurnTime(this.furnaceItemStacks.get(1));
        this.loadFromNbt(compound);
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
        return this.saveToNbt(compound);
    }

    public void loadFromNbt(NBTTagCompound compound) {
        this.furnaceItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound) && compound.hasKey("Items", 9)) {
            ItemStackHelper.loadAllItems(compound, this.furnaceItemStacks);
        }
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    public NBTTagCompound saveToNbt(NBTTagCompound compound) {
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.furnaceItemStacks, false);
        }
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        if (!compound.hasKey("Lock") && this.isLocked()) {
            this.getLockCode().toNBT(compound);
        }
        return compound;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.furnaceItemStacks;
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

    public boolean isCleared() {
        return this.hasBeenCleared;
    }

    public float getProgress(float p_190585_1_) {
        return this.progressOld + (this.progress - this.progressOld) * p_190585_1_;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumDyeColor getColor() {
        if (this.color == null) {
            this.color = BlockShulkerFurnace.getColorFromBlock(this.getBlockType());
        }
        return this.color;
    }

    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 10, this.getUpdateTag());
    }

    public boolean isDestroyedByCreativePlayer() {
        return this.destroyedByCreativePlayer;
    }

    public void setDestroyedByCreativePlayer(boolean p_190579_1_) {
        this.destroyedByCreativePlayer = p_190579_1_;
    }

    public boolean shouldDrop() {
        return !this.isDestroyedByCreativePlayer() || !this.isEmpty() || this.hasCustomName() || this.lootTable != null;
    }

    @Override
    protected IItemHandler createUnSidedHandler() {
        return new SidedInvWrapper(this, EnumFacing.UP);
    }

    @Override
    public void tick() {
    }

    public static enum AnimationStatus {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

    }
}

