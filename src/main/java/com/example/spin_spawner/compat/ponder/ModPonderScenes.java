package com.example.spin_spawner.compat.ponder;

import com.example.spin_spawner.block.ModBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.Ponder;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class ModPonderScenes {
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ResourceLocation emptyScene = ResourceLocation.fromNamespaceAndPath(Ponder.MOD_ID, "debug/scene_1");
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(ModBlocks.SPIN_SPAWNER)
                .addStoryBoard(emptyScene, SpinSpawnerScene::spawner, AllCreatePonderTags.DISPLAY_TARGETS)
                .addStoryBoard(emptyScene, SpinSpawnerScene::generator, AllCreatePonderTags.DISPLAY_SOURCES);
    }
}