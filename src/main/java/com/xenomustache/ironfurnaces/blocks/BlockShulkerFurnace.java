package com.xenomustache.ironfurnaces.blocks;

import com.xenomustache.ironfurnaces.IronFurnaces;
import com.xenomustache.ironfurnaces.tileentity.TileEntityShulkerFurnace;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class BlockShulkerFurnace extends BlockContainer {
    public static final PropertyEnum<EnumFacing> FACING = PropertyDirection.create("facing");
    protected String name;
    protected static boolean isBurning;
    protected final EnumDyeColor color;

    public BlockShulkerFurnace(String name, boolean isBurning, EnumDyeColor colorIn) {
        super(Material.ROCK, MapColor.AIR);
        this.name = name;
        BlockShulkerFurnace.isBurning = isBurning;
        this.color = colorIn;
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setHardness(3.0f);
        this.setResistance(5.0f);
        this.setHarvestLevel("pickaxe", 1);
        this.setCreativeTab(IronFurnaces.creativeTab);
    }

    public void registerItemModel(Item itemBlock) {
        IronFurnaces.proxy.registerItemRenderer(itemBlock, 0, this.name);
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(this.getRegistryName());
    }

    @Override
    public BlockShulkerFurnace setCreativeTab(CreativeTabs tab) {
        if (!isBurning) {
            super.setCreativeTab(tab);
        } else {
            super.setCreativeTab(null);
        }
        return this;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);
            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }
            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (isBurning) {
            EnumFacing enumfacing = stateIn.getValue(FACING);
            double d0 = (double) pos.getX() + 0.5;
            double d1 = (double) pos.getY() + rand.nextDouble() * 6.0 / 16.0;
            double d2 = (double) pos.getZ() + 0.5;
            double d4 = rand.nextDouble() * 0.6 - 0.3;
            if (rand.nextDouble() < 0.1) {
                worldIn.playSound((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
            }
            switch (enumfacing) {
                case WEST: {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - 0.52, d1, d2 + d4, 0.0, 0.0, 0.0, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - 0.52, d1, d2 + d4, 0.0, 0.0, 0.0, new int[0]);
                    break;
                }
                case EAST: {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.52, d1, d2 + d4, 0.0, 0.0, 0.0, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.52, d1, d2 + d4, 0.0, 0.0, 0.0, new int[0]);
                    break;
                }
                case NORTH: {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 - 0.52, 0.0, 0.0, 0.0, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 - 0.52, 0.0, 0.0, 0.0, new int[0]);
                    break;
                }
                case SOUTH: {
                    worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, d1, d2 + 0.52, 0.0, 0.0, 0.0, new int[0]);
                    worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, d1, d2 + 0.52, 0.0, 0.0, 0.0, new int[0]);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityShulkerFurnace(this.color);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        if (playerIn.isSpectator()) {
            return true;
        }
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityShulkerFurnace) {
            AxisAlignedBB axisalignedbb;
            EnumFacing enumfacing = state.getValue(FACING);
            boolean flag = ((TileEntityShulkerFurnace) tileentity).getAnimationStatus() == TileEntityShulkerFurnace.AnimationStatus.CLOSED ? !worldIn.collidesWithAnyBlock((axisalignedbb = FULL_BLOCK_AABB.expand(0.5f * (float) enumfacing.getFrontOffsetX(), 0.5f * (float) enumfacing.getFrontOffsetY(), 0.5f * (float) enumfacing.getFrontOffsetZ()).contract(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY(), enumfacing.getFrontOffsetZ())).offset(pos.offset(enumfacing))) : true;
            if (flag) {
                playerIn.addStat(StatList.OPEN_SHULKER_BOX);
                playerIn.displayGUIChest((IInventory) tileentity);
            }
            return true;
        }
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (worldIn.getTileEntity(pos) instanceof TileEntityShulkerFurnace) {
            TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace) worldIn.getTileEntity(pos);
            tileentityshulkerfurnace.setDestroyedByCreativePlayer(player.capabilities.isCreativeMode);
            tileentityshulkerfurnace.fillWithLoot(player);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileentity;
        if (stack.hasDisplayName() && (tileentity = worldIn.getTileEntity(pos)) instanceof TileEntityShulkerFurnace) {
            ((TileEntityShulkerFurnace) tileentity).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityShulkerFurnace) {
            TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace) tileentity;
            if (!tileentityshulkerfurnace.isCleared() && tileentityshulkerfurnace.shouldDrop()) {
                ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound.setTag("BlockEntityTag", ((TileEntityShulkerFurnace) tileentity).saveToNbt(nbttagcompound1));
                itemstack.setTagCompound(nbttagcompound);
                if (tileentityshulkerfurnace.hasCustomName()) {
                    itemstack.setStackDisplayName(tileentityshulkerfurnace.getName());
                    tileentityshulkerfurnace.setCustomName("");
                }
                BlockShulkerFurnace.spawnAsEntity(worldIn, pos, itemstack);
            }
            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound != null && nbttagcompound.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("BlockEntityTag");
            if (nbttagcompound1.hasKey("LootTable", 8)) {
                tooltip.add("???????");
            }
            if (nbttagcompound1.hasKey("Items", 9)) {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(nbttagcompound1, nonnulllist);
                int i = 0;
                int j = 0;
                for (ItemStack itemstack : nonnulllist) {
                    if (itemstack.isEmpty()) continue;
                    ++j;
                    if (i > 4) continue;
                    ++i;
                    tooltip.add(String.format("%s x%d", itemstack.getDisplayName(), itemstack.getCount()));
                }
                if (j - i > 0) {
                    tooltip.add(String.format(TextFormatting.ITALIC + I18n.translateToLocal("container.shulkerFurnace.more"), j - i));
                }
            }
        }
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tileentity = source.getTileEntity(pos);
        return tileentity instanceof TileEntityShulkerFurnace ? ((TileEntityShulkerFurnace) tileentity).getBoundingBox(state) : FULL_BLOCK_AABB;
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos));
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        ItemStack itemstack = super.getItem(worldIn, pos, state);
        TileEntityShulkerFurnace tileentityshulkerfurnace = (TileEntityShulkerFurnace) worldIn.getTileEntity(pos);
        NBTTagCompound nbttagcompound = tileentityshulkerfurnace.saveToNbt(new NBTTagCompound());
        if (!nbttagcompound.hasNoTags()) {
            itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
        }
        return itemstack;
    }

    @SideOnly(value = Side.CLIENT)
    public static EnumDyeColor getColorFromItem(Item itemIn) {
        return BlockShulkerFurnace.getColorFromBlock(Block.getBlockFromItem(itemIn));
    }

    @SideOnly(value = Side.CLIENT)
    public static EnumDyeColor getColorFromBlock(Block blockIn) {
        return blockIn instanceof BlockShulkerFurnace ? ((BlockShulkerFurnace) blockIn).getColor() : EnumDyeColor.PURPLE;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        state = this.getActualState(state, worldIn, pos);
        EnumFacing enumfacing = state.getValue(FACING);
        TileEntityShulkerFurnace.AnimationStatus tileentityshulkerfurnace$animationstatus = ((TileEntityShulkerFurnace) worldIn.getTileEntity(pos)).getAnimationStatus();
        return tileentityshulkerfurnace$animationstatus != TileEntityShulkerFurnace.AnimationStatus.CLOSED && (tileentityshulkerfurnace$animationstatus != TileEntityShulkerFurnace.AnimationStatus.OPENED || enumfacing != face.getOpposite() && enumfacing != face) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SideOnly(value = Side.CLIENT)
    public EnumDyeColor getColor() {
        return this.color;
    }
}

