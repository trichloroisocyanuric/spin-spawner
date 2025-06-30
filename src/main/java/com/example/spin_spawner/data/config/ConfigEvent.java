package com.example.spin_spawner.data.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ConfigEvent {
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        Config.load(configEvent.getConfig());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        Config.load(configEvent.getConfig());
    }
}
