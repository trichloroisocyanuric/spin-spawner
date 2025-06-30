package com.example.spin_spawner.data;

import com.example.spin_spawner.SpinSpawnerMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> type, String name) {
        return TagKey.create(type, ResourceLocation.fromNamespaceAndPath(SpinSpawnerMod.MODID, name));
    }

    public static class Blocks {
        public static final TagKey<Block> INHERIT_BLOCK_ENTITY_DATA = create("inherit_block_entity");


        private static TagKey<Block> create(String name) {
            return ModTags.create(Registries.BLOCK, name);
        }
    }
}