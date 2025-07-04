package com.example.spin_spawner.compat.jade;

import com.example.spin_spawner.block.entity.SpinSpawnerBlockEntity;
import snownee.jade.addon.vanilla.MobSpawnerProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class SpinSpawnerProvider extends MobSpawnerProvider implements IBlockComponentProvider {
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof SpinSpawnerBlockEntity spawner) {
            appendTooltip(tooltip, spawner.getSpawner().getOrCreateDisplayEntity(
                    accessor.getLevel(), accessor.getPosition()), accessor.getBlock().getName()
            );
            spawner.apothProxy.addTooltip(tooltip::add);
        }
    }
}