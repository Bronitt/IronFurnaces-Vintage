package com.xenomustache.ironfurnaces;

import com.xenomustache.ironfurnaces.blocks.IFBlocks;
import com.xenomustache.ironfurnaces.proxy.CommonProxy;
import com.xenomustache.ironfurnaces.tileentity.TileEntityDiamondFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGlassFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityGoldFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityIronFurnace;
import com.xenomustache.ironfurnaces.tileentity.TileEntityObsidianFurnace;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.jetbrains.annotations.NotNull;
import xenomustache.ironfurnaces.Tags;

@Mod(
        modid = Tags.MOD_ID,
        name = Tags.MOD_NAME,
        version = Tags.VERSION,
        acceptedMinecraftVersions = ForgeVersion.mcVersion
)
public class IronFurnaces {
    public static final String MODID = Tags.MOD_ID;
    public static final String NAME = Tags.MOD_NAME;
    public static final String VERSION = Tags.VERSION;
    public static final ModTab creativeTab = new ModTab();

    @SidedProxy(
            serverSide = "com.xenomustache.ironfurnaces.proxy.CommonProxy",
            clientSide = "com.xenomustache.ironfurnaces.proxy.ClientProxy"
    )
    public static CommonProxy proxy;

    @Mod.Instance(Tags.MOD_ID)
    public static IronFurnaces instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileEntityIronFurnace.class, new ResourceLocation(Tags.MOD_ID, "iron_furnace"));
        GameRegistry.registerTileEntity(TileEntityGoldFurnace.class, new ResourceLocation(Tags.MOD_ID, "gold_furnace"));
        GameRegistry.registerTileEntity(TileEntityDiamondFurnace.class, new ResourceLocation(Tags.MOD_ID, "diamond_furnace"));
        GameRegistry.registerTileEntity(TileEntityGlassFurnace.class, new ResourceLocation(Tags.MOD_ID, "glass_furnace"));
        GameRegistry.registerTileEntity(TileEntityObsidianFurnace.class, new ResourceLocation(Tags.MOD_ID, "obsidian_furnace"));
    }

    public static class ModTab extends CreativeTabs {
        public ModTab() {
            super(IronFurnaces.MODID);
        }

        @Override
        public @NotNull ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(IFBlocks.ironFurnaceIdle));
        }
    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            IFBlocks.register(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IFBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerItems(ModelRegistryEvent event) {
            IFBlocks.registerModels();
        }
    }
}

