package com.example.spin_spawner.compat.apothic_spawners;

import com.example.spin_spawner.block.entity.SpinSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface ApothSpawnerTileProvider {
    @Nullable
    ApothSpawnerTileProvider INSTANCE = ModList.get().isLoaded("apothic_spawners") ?
            null : new ApothSpawnerTileProvider() {
    };

    static ApothSpawnerTileProvider getInstance(SpinSpawnerBlockEntity be) {
        return INSTANCE != null ? INSTANCE : new ApothSpawnerTileProxy() {
            @Override
            public @NotNull BaseSpawner getSpawner() {
                return be.getSpawner();
            }

            @Override
            public void setChanged() {
                be.setChanged();
            }

            @Override
            public @Nullable Level getLevel() {
                return be.getLevel();
            }
        };
    }

    default void setApothEntityState(Entity entity) {
    }

    default void white(CompoundTag tag) {
    }

    default void read(CompoundTag tag) {
    }

    default boolean useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    default void addTooltip(Consumer<Component> components) {
    }
}
