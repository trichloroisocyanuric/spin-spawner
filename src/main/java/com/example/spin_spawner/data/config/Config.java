package com.example.spin_spawner.data.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Config {
    public static final ModConfigSpec serverSpec;
    public static final Server SERVER;
    public static float baseRpm;
    public static float minRpm;
    public static float stressPerRpm = 16;
    public static int generationSpeed = 64;
    public static boolean loadExternalEntity;
    public static HashSet<String> itemBlacklist;

    static {
        final Pair<Server, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static void load(ModConfig modConfig) {
        if (modConfig.getType() == ModConfig.Type.SERVER) {
            baseRpm = SERVER.baseRpm.get().floatValue();
            minRpm = SERVER.minRpm.get().floatValue();
            stressPerRpm = SERVER.stressPerRpm.get().floatValue();
            generationSpeed = SERVER.generationSpeed.get();
            loadExternalEntity = SERVER.loadExternalEntity.get();
            itemBlacklist = new HashSet<>(SERVER.itemBlacklist.get());
        }
    }

    public static class Server {
        public final ModConfigSpec.DoubleValue baseRpm;
        public final ModConfigSpec.DoubleValue minRpm;
        public final ModConfigSpec.DoubleValue stressPerRpm;
        public final ModConfigSpec.IntValue generationSpeed;
        public final ModConfigSpec.BooleanValue loadExternalEntity;
        public final ModConfigSpec.ConfigValue<List<? extends String>> itemBlacklist;

        private Server(ModConfigSpec.Builder builder) {
            baseRpm = builder.comment("The base rpm at which the spin spawner operates at normal speed.").defineInRange("baseRpm", 128.0, 1.0, 65536.0);
            minRpm = builder.comment("The minimum rpm of spin spawner").defineInRange("minRpm", 64.0, 0.0, 256.0);
            stressPerRpm = builder.comment("Stress impact and capacity of spin spawner").defineInRange("stressPerRpm", 16.0, 1.0, 4096.0);
            generationSpeed = builder.comment("Generation mod speed").defineInRange("generationSpeed", 64, 1, 256);
            loadExternalEntity = builder.comment("Spin Spawner can be right-click to change entity by any item that data component contain \"entity_data\", which can raise UNEXPECTED behavior").define("loadExternalEntity", false);
            itemBlacklist = builder.comment("Blacklist of change spawner entity").defineListAllowEmpty("itemBlacklist", Collections::emptyList, () -> "", Server::elementValidator);
        }

        private static boolean elementValidator(Object obj) {
            if (obj instanceof String itemId) {
                ResourceLocation resourceLocation = ResourceLocation.tryParse(itemId);
                if (resourceLocation != null) {
                    return BuiltInRegistries.ITEM.containsKey(resourceLocation);
                }
            }
            return false;
        }
    }
}