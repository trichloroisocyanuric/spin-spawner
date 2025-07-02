package com.example.spin_spawner;

import com.example.spin_spawner.block.ModBlocks;
import com.example.spin_spawner.block.entity.ModBlockEntities;
import com.example.spin_spawner.block.entity.renderer.ModPonderPlugin;
import com.example.spin_spawner.data.ModLang;
import com.example.spin_spawner.data.config.Config;
import com.example.spin_spawner.data.config.ConfigEvent;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.api.stress.BlockStressValues;
import com.tterrag.registrate.Registrate;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(SpinSpawnerMod.MODID)
public class SpinSpawnerMod {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "spin_spawner";
    public static final Registrate REGISTRATE = Registrate.create(MODID).defaultCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey());

    public SpinSpawnerMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register();
        ModBlockEntities.register();
        ModLang.register();

        modEventBus.register(ConfigEvent.class);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        modEventBus.addListener(SpinSpawnerMod::onCommonSetup);
        modEventBus.addListener(SpinSpawnerMod::onClientSetup);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
        BlockStressValues.IMPACTS.register(ModBlocks.SPIN_SPAWNER.get(), () -> Config.stressPerRpm);
        BlockStressValues.CAPACITIES.register(ModBlocks.SPIN_SPAWNER.get(), () -> Config.stressPerRpm);
        BlockStressValues.setGeneratorSpeed(Config.generationSpeed).accept(ModBlocks.SPIN_SPAWNER.get());
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        try {
            PonderIndex.addPlugin(new ModPonderPlugin());
        } catch (Exception e) {
            LOGGER.warn("failed to load ponder: {}", e.toString());
        }
    }
}
