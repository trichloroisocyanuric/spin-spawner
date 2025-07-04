package com.example.spin_spawner;

import com.example.spin_spawner.block.ModBlocks;
import com.example.spin_spawner.block.entity.ModBlockEntities;
import com.example.spin_spawner.compat.ponder.ModPonderPlugin;
import com.example.spin_spawner.data.ModLang;
import com.example.spin_spawner.data.config.ModConfigs;
import com.mojang.logging.LogUtils;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.api.stress.BlockStressValues;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
        ModConfigs.register(modContainer);

        modEventBus.addListener(SpinSpawnerMod::onCommonSetup);
        modEventBus.addListener(SpinSpawnerMod::onClientSetup);
        modEventBus.addListener(EventPriority.HIGHEST, SpinSpawnerMod::onDataGather);
    }

    private static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.IMPACTS.register(ModBlocks.SPIN_SPAWNER.get(), ModConfigs.server().stressPerRpm::get);
            BlockStressValues.CAPACITIES.register(ModBlocks.SPIN_SPAWNER.get(), ModConfigs.server().stressPerRpm::get);
        });
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                PonderIndex.addPlugin(new ModPonderPlugin());
            } catch (Exception e) {
                LOGGER.warn("failed to load ponder: {}", e.toString());
            }
        });
    }

    private static void onDataGather(GatherDataEvent event) {
        if (event.getMods().contains(MODID)) {
            PonderIndex.addPlugin(new ModPonderPlugin());
            REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
                PonderIndex.getLangAccess().provideLang(SpinSpawnerMod.MODID, provider::add);
            });
        }
    }
}
