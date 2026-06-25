package com.xenomustache.ironfurnaces;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xenomustache.ironfurnaces.Tags;

@Config(modid = Tags.MOD_ID)
public class ModConfig {
    @Config.RequiresMcRestart
    @Config.Name("Iron Furnace Cook Time")
    public static int IronFurnaceCookTime = 100;
    @Config.RequiresMcRestart
    @Config.Name("Gold Furnace Cook Time")
    public static int GoldFurnaceCookTime = 66;
    @Config.RequiresMcRestart
    @Config.Name("Diamond Furnace Cook Time")
    public static int DiamondFurnaceCookTime = 50;
    @Config.RequiresMcRestart
    @Config.Name("Obsidian Furnace Cook Time")
    public static int ObsidianFurnaceCookTime = 50;
    @Config.RequiresMcRestart
    @Config.Name("Crystal Furnace Cook Time")
    public static int CrystalFunraceCookTime = 40;

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    private static class EventHandler {
        private EventHandler() {
        }

        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals("ironfurnaces")) {
                ConfigManager.sync("ironfurnaces", Config.Type.INSTANCE);
            }
        }
    }
}

