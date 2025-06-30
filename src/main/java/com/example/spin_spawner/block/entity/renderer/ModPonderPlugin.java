package com.example.spin_spawner.block.entity.renderer;

import com.example.spin_spawner.SpinSpawnerMod;
import com.example.spin_spawner.compat.ponder.ModPonderScenes;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;


public class ModPonderPlugin implements PonderPlugin {

    @Override
    public @Nonnull String getModId() {
        return SpinSpawnerMod.MODID;
    }

    @Override
    public void registerScenes(@Nonnull PonderSceneRegistrationHelper<ResourceLocation> helper) {
        ModPonderScenes.register(helper);
    }
}