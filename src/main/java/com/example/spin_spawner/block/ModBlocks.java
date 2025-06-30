package com.example.spin_spawner.block;

import com.example.spin_spawner.data.ModTags;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Blocks;

import static com.example.spin_spawner.SpinSpawnerMod.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;


public class ModBlocks {
    public static final BlockEntry<SpinSpawnerBlock> SPIN_SPAWNER = REGISTRATE
            .block("spin_spawner", SpinSpawnerBlock::new)
            .initialProperties(() -> Blocks.SPAWNER)
            .transform(pickaxeOnly())
            .tag(ModTags.Blocks.INHERIT_BLOCK_ENTITY_DATA)
            .blockstate((ctx, prov) ->
                    prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(prov.modLoc(ctx.getName())))
            )
            .simpleItem()
            .register();

    public static void register() {
    }
}
