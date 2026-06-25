package com.xenomustache.ironfurnaces.blocks;

import com.xenomustache.ironfurnaces.IronFurnaces;
import com.xenomustache.ironfurnaces.blocks.IFBlocks;
import com.xenomustache.ironfurnaces.tileentity.TileEntityDiamondFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGlassFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGoldFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityIronFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityObsidianFurnace;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDiamondFurnace
extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    protected String name;
    protected boolean isBurning;

    public BlockDiamondFurnace(String name, boolean isBurning) {
        super(Material.ROCK);
        this.name = name;
        this.isBurning = isBurning;
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
    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockDiamondFurnace setCreativeTab(CreativeTabs tab) {
        if (!this.isBurning) {
            super.setCreativeTab(tab);
        } else if (this.isBurning) {
            super.setCreativeTab(null);
        }
        return this;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(IFBlocks.diamondFurnaceIdle);
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
            EnumFacing enumfacing = (EnumFacing)state.getValue((IProperty)FACING);
            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                enumfacing = EnumFacing.SOUTH;
            } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                enumfacing = EnumFacing.NORTH;
            } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                enumfacing = EnumFacing.EAST;
            } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                enumfacing = EnumFacing.WEST;
            }
            worldIn.setBlockState(pos, state.withProperty((IProperty)FACING, (Comparable)enumfacing), 2);
        }
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (this.isBurning) {
            EnumFacing enumfacing = (EnumFacing)stateIn.getValue((IProperty)FACING);
            double d0 = (double)pos.getX() + 0.5;
            double d1 = (double)pos.getY() + rand.nextDouble() * 6.0 / 16.0;
            double d2 = (double)pos.getZ() + 0.5;
            double d4 = rand.nextDouble() * 0.6 - 0.3;
            if (rand.nextDouble() < 0.1) {
                worldIn.playSound((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityIronFurnace) {
            playerIn.displayGUIChest((IInventory) tileentity);
            playerIn.addStat(StatList.FURNACE_INTERACTION);
        } else if (tileentity instanceof TileEntityGoldFurnace) {
            playerIn.displayGUIChest((IInventory) tileentity);
            playerIn.addStat(StatList.FURNACE_INTERACTION);
        } else if (tileentity instanceof TileEntityDiamondFurnace) {
            playerIn.displayGUIChest((IInventory) tileentity);
            playerIn.addStat(StatList.FURNACE_INTERACTION);
        } else if (tileentity instanceof TileEntityGlassFurnace) {
            playerIn.displayGUIChest((IInventory) tileentity);
            playerIn.addStat(StatList.FURNACE_INTERACTION);
        } else if (tileentity instanceof TileEntityObsidianFurnace) {
            playerIn.displayGUIChest((IInventory) tileentity);
            playerIn.addStat(StatList.FURNACE_INTERACTION);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDiamondFurnace();
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty((IProperty)FACING, (Comparable)placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileentity;
        worldIn.setBlockState(pos, state.withProperty((IProperty)FACING, (Comparable)placer.getHorizontalFacing().getOpposite()), 2);
        if (stack.hasDisplayName() && (tileentity = worldIn.getTileEntity(pos)) instanceof TileEntityDiamondFurnace) {
            ((TileEntityDiamondFurnace)tileentity).setCustomInventoryName(stack.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity;
        if (!TileEntityDiamondFurnace.keepInventory && (tileentity = worldIn.getTileEntity(pos)) instanceof TileEntityDiamondFurnace) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(IFBlocks.diamondFurnaceIdle);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty((IProperty)FACING, (Comparable)enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue((IProperty)FACING)).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty((IProperty)FACING, (Comparable)rot.rotate((EnumFacing)state.getValue((IProperty)FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue((IProperty)FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }
}

