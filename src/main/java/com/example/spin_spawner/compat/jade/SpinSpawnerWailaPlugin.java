package com.example.spin_spawner.compat.jade;

import com.example.spin_spawner.block.SpinSpawnerBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SpinSpawnerWailaPlugin implements IWailaPlugin {
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new SpinSpawnerProvider(), SpinSpawnerBlock.class);
    }
}
