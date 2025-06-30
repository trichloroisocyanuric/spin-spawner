package com.example.spin_spawner.block.entity;

import com.example.spin_spawner.block.ModBlocks;
import com.example.spin_spawner.block.entity.renderer.SpinSpawnerBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.example.spin_spawner.SpinSpawnerMod.REGISTRATE;

public class ModBlockEntities {
    public static final BlockEntityEntry<SpinSpawnerBlockEntity> SPIN_SPAWNER = REGISTRATE
            .blockEntity("spin_spawner", SpinSpawnerBlockEntity::new)
            .validBlocks(ModBlocks.SPIN_SPAWNER)
            .renderer(() -> SpinSpawnerBlockEntityRenderer::new)
            .register();

    public static void register() {
    }
}
