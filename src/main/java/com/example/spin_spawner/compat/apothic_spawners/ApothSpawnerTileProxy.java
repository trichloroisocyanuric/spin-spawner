package com.example.spin_spawner.compat.apothic_spawners;

import com.mojang.serialization.Codec;
import dev.shadowsoffire.apothic_spawners.ASObjects;
import dev.shadowsoffire.apothic_spawners.ApothicSpawners;
import dev.shadowsoffire.apothic_spawners.block.ApothSpawnerTile;
import dev.shadowsoffire.apothic_spawners.modifiers.SpawnerModifier;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStat;
import dev.shadowsoffire.apothic_spawners.stats.SpawnerStats;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

import static com.example.spin_spawner.SpinSpawnerMod.LOGGER;

public abstract class ApothSpawnerTileProxy extends ApothSpawnerTile implements ApothSpawnerTileProvider {
    public ApothSpawnerTileProxy() {
        super(BlockPos.ZERO, Blocks.SPAWNER.defaultBlockState());
        this.level = getLevel();
        this.spawner = getSpawner();
    }

    @Override
    public abstract @Nonnull BaseSpawner getSpawner();

    @Override
    public abstract void setChanged();

    @Override
    public abstract @Nullable Level getLevel();

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestModelDataUpdate() {
        assert false;
    }

    @Override
    public boolean hasBeenModified() {
        return true;
    }

    private <T> T getStatValue(SpawnerStat<T> stat) {
        return stat.getValue(this);
    }

    @Override
    public void setApothEntityState(Entity entity) {
        entity.getSelfAndPassengers().forEach(selfOrPassenger -> {
            if (this.getStatValue(SpawnerStats.NO_AI) && selfOrPassenger instanceof Mob mob) {
                mob.setNoAi(true);
                mob.getPersistentData().putBoolean("apotheosis:movable", true);
            }

            if (this.getStatValue(SpawnerStats.YOUTHFUL) && selfOrPassenger instanceof Mob mob) {
                mob.setBaby(true);
            }

            if (this.getStatValue(SpawnerStats.SILENT)) {
                selfOrPassenger.setSilent(true);
            }

            if (this.getStatValue(SpawnerStats.INITIAL_HEALTH) != 1 && selfOrPassenger instanceof LivingEntity living) {
                living.setHealth(living.getHealth() * this.getStatValue(SpawnerStats.INITIAL_HEALTH));
            }

            if (this.getStatValue(SpawnerStats.BURNING) && !selfOrPassenger.fireImmune()) {
                selfOrPassenger.setRemainingFireTicks(Integer.MAX_VALUE);
            }

            if (this.getStatValue(SpawnerStats.ECHOING) > 0) {
                selfOrPassenger.getPersistentData().putInt(SpawnerStats.ECHOING.getId().toString(), this.getStatValue(SpawnerStats.ECHOING));
            }
        });
    }

    @Override
    public void read(CompoundTag tag) {
        CompoundTag stats = tag.getCompound("stats");
        for (String key : stats.getAllKeys()) {
            SpawnerStat<?> stat = SpawnerStats.REGISTRY.get(ResourceLocation.tryParse(key));
            if (stat != null) {
                Tag value = stats.get(key);
                try {
                    Object realValue = stat.getValueCodec().decode(NbtOps.INSTANCE, value).getOrThrow().getFirst();
                    this.customStats.put(stat, realValue);
                } catch (Exception ex) {
                    LOGGER.error("Failed loading spawner stat {}", key, ex);
                }
            }
        }
    }

    @Override
    public void white(CompoundTag tag) {
        if (!customStats.isEmpty()) {
            CompoundTag stats = new CompoundTag();
            this.customStats.forEach((stat, value) -> {
                try {
                    @SuppressWarnings("unchecked")
                    Tag encoded = ((Codec<Object>) stat.getValueCodec()).encodeStart(NbtOps.INSTANCE, value).getOrThrow();
                    stats.put(stat.getId().toString(), encoded);
                } catch (Exception ex) {
                    LOGGER.error("Failed saving spawner stat {}", stat.getId(), ex);
                }
            });
            tag.put("stats", stats);
        }
    }

    @Override
    public boolean useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack otherStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        SpawnerModifier match = SpawnerModifier.findMatch(this, stack, otherStack);
        if (match != null && match.apply(this)) {
            if (world.isClientSide) return true;

            if (!player.isCreative()) {
                stack.shrink(1);
                if (match.consumesOffhand()) otherStack.shrink(1);
            }

            ASObjects.MODIFIER_TRIGGER.trigger((ServerPlayer) player, this, match);
            world.sendBlockUpdated(pos, state, state, 3);
            return true;
        }
        return false;
    }

    @Override
    public void addTooltip(Consumer<Component> list) {
        if (Screen.hasControlDown()) {
            SpawnerStats.generateTooltip(this, list);
        } else {
            list.accept(ApothicSpawners.lang("misc", "ctrl_stats"));
        }
    }
}
