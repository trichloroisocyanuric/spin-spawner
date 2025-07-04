package com.example.spin_spawner.data.config;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber
public class ModConfigs {
    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    //    private static CClient client;
//    private static CCommon common;
    private static ServerConfig server;

//    public static CClient client() {
//        return client;
//    }
//
//    public static CCommon common() {
//        return common;
//    }

    public static ServerConfig server() {
        return server;
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register(ModContainer container) {
//        client = register(CClient::new, ModConfig.Type.CLIENT);
//        common = register(CCommon::new, ModConfig.Type.COMMON);
        server = register(ServerConfig::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet())
            container.registerConfig(pair.getKey(), pair.getValue().specification);
    }

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig()
                    .getSpec())
                config.onLoad();
        Config.load();
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        for (ConfigBase config : CONFIGS.values())
            if (config.specification == event.getConfig().getSpec())
                config.onReload();
        Config.load();
    }
}
