package com.example.spin_spawner.compat.ponder;

import com.example.spin_spawner.SpinSpawnerMod;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
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

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        helper.registerSharedText("see_other", "%1$s");
        helper.registerSharedText("this_is", "This is a Spin Spawner");
        helper.registerSharedText("upgrade_ponder", "upgrade ponder to 1.0.58+ to view.");
    }
}
