package com.example.spin_spawner.data.config;

import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.example.spin_spawner.SpinSpawnerMod.LOGGER;

public class ServerConfig extends ConfigBase {

    public ConfigFloat baseRpm = f(128.f, 1.f, 65536.f, "baseRpm", "The base rpm at which the spin spawner operates at normal speed.");
    public ConfigFloat minRpm = f(64.f, 0.f, 256.f, "minRpm", "The minimum rpm of spin spawner");
    public ConfigFloat stressPerRpm = f(16.f, 1.f, 4096.f, "stressPerRpm", "Stress impact and capacity of spin spawner");
    public ConfigInt generationSpeed = i(64, 1, 256, "generationSpeed", "Generation mod speed");
    public ConfigBool loadExternalEntity = b(false, "loadExternalEntity", "Spin Spawner can be right-click to change entity by any item that data component contain \"entity_data\", which can raise UNEXPECTED behavior");
    public ConfigList<String> itemBlacklist = list(Collections::emptyList, "", ServerConfig::isItem, "itemBlacklist", "Blacklist of change spawner entity");

    private static boolean isItem(Object obj) {
        boolean isItem = false;
        if (obj instanceof String itemId) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(itemId);
            if (resourceLocation != null) {
                isItem = BuiltInRegistries.ITEM.containsKey(resourceLocation);
            }
        }
        LOGGER.warn("No such item: \"{}\"", obj);
        return isItem;
    }

    @Override
    public @Nonnull String getName() {
        return "server";
    }

    protected <T> ConfigList<T> list(Supplier<List<? extends T>> current, T newElement, Predicate<Object> elementValidator, String name, String... comment) {
        return new ConfigList<T>(name, current, () -> newElement, elementValidator, comment);
    }

    public class ConfigList<T> extends CValue<List<? extends T>, ConfigValue<List<? extends T>>> {
        public ConfigList(String name, Supplier<List<? extends T>> def, Supplier<T> newElementSupplier, Predicate<Object> elementValidator, String... comment) {
            super(name, builder -> builder.defineListAllowEmpty(name, def, newElementSupplier, elementValidator), comment);
        }
    }
}
