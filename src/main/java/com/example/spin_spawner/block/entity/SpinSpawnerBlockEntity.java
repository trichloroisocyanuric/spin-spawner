package com.example.spin_spawner.block.entity;

import com.example.spin_spawner.block.ModBlocks;
import com.example.spin_spawner.BaseSpinSpawner;
import com.example.spin_spawner.data.config.Config;
import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.GOLD;


public class SpinSpawnerBlockEntity extends GeneratingKineticBlockEntity implements Spawner {
    private final BaseSpinSpawner spawner;
    private float entitySize = 0;
    private boolean generating = false;
    private boolean requiredUpdate = false;

    public SpinSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(200);

        this.spawner = new BaseSpinSpawner() {
            @Override
            protected float getKineticSpeed() {
                return getSpeed();
            }

            @Override
            protected boolean isGenerating() {
                return SpinSpawnerBlockEntity.this.generating;
            }

            @Override
            public void broadcastEvent(Level level, BlockPos pos, int eventId) {
                level.blockEvent(pos, ModBlocks.SPIN_SPAWNER.get(), eventId, 0);
            }

            @Override
            public Either<BlockEntity, Entity> getOwner() {
                return Either.left(SpinSpawnerBlockEntity.this);
            }
        };
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToTooltip(tooltip, isPlayerSneaking);

        List<Component> hints = new ArrayList<>();
        if (entitySize == 0) {
            hints.add(CreateLang.translateDirect(BaseSpinSpawner.Result.INVALID_ENTITY.langKey));
        } else {
            if (generating) {
                if (entitySize < 0) {
                    hints.addAll(CreateLang.translatedOptions("gui.spawner", "incorrect_spawn_rule_1", "incorrect_spawn_rule_2"));
                }
            } else {
                if (spawner.lastResult != BaseSpinSpawner.Result.SUCCEED) {
                    hints.add(CreateLang.translateDirect(spawner.lastResult.langKey, spawner.spawnRange));
                }
            }
        }

        if (!hints.isEmpty()) {
            if (added) tooltip.add(CommonComponents.EMPTY);
            CreateLang.translate("gui.spawner." + (generating ? "generate" : "spawn") + "_failed").text(":").style(GOLD).forGoggles(tooltip);
            for (Component hint : hints) {
                List<Component> cutString = TooltipHelper.cutTextComponent(hint, FontHelper.Palette.GRAY_AND_WHITE);
                for (Component component : cutString) CreateLang.builder().add(component.copy()).forGoggles(tooltip);
            }
            added = true;
        }

        return added;
    }

    @Override
    public void tick() {
        super.tick();

        if (requiredUpdate) {
            requiredUpdate = false;
            updateGeneratedRotation();
        }

        if (level == null || entitySize == 0 || !isSpeedRequirementFulfilled()) return;

        if (level.isClientSide) {
            spawner.clientTick(level, worldPosition);
        } else {
            if (!generating) {
                spawner.serverTick((ServerLevel) level, worldPosition);
            }
        }

    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (generating && level != null && !level.isClientSide) {
            updateEntitySize(false);
        }
    }

    private void updateEntitySize(boolean force) {
        if (level == null || level.isClientSide) return;
        float newEntitySize = entitySize;
        if (spawner.checkSpawnRules((ServerLevel) level, worldPosition)) {
            newEntitySize = Mth.abs(newEntitySize);
        } else {
            newEntitySize = -Mth.abs(newEntitySize);
        }
        if (force || newEntitySize != entitySize) {
            entitySize = newEntitySize;
            requiredUpdate = true;
        }
    }

    public void fakeGenerating() {
        generating = true;
        entitySize = 0.01f;
        requiredUpdate = true;
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return generating || super.isSpeedRequirementFulfilled();
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        spawner.load(level, worldPosition, tag);

        if (!wasMoved) {
            generating = tag.getBoolean("Generating");
            entitySize = tag.getFloat("EntitySize");
        }

        if (entitySize == 0) {
            entitySize = spawner.getEntitySize();
            if (generating) updateEntitySize(true);
        }
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (entitySize != 0) tag.putFloat("EntitySize", entitySize);
        tag.putBoolean("Generating", generating);

        spawner.save(tag);

        super.write(tag, registries, clientPacket);
    }

    @Override
    public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
        CompoundTag compoundtag = new CompoundTag();
        spawner.save(compoundtag);
        compoundtag.remove("Delay");
        compoundtag.remove("LastResult");
        BlockItem.setBlockEntityData(stack, this.getType(), compoundtag);
        stack.applyComponents(this.collectComponents());
    }

    @Override
    public void setEntityId(EntityType<?> entityType, RandomSource random) {
        spawner.setEntityId(entityType, level, random, worldPosition);
        entitySize = spawner.getEntitySize();
        updateEntitySize(true);
    }

    public boolean hasSpawnData() {
        return spawner.hasSpawnData();
    }

    public BaseSpawner getSpawner() {
        return this.spawner;
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (this.level != null) {
            return this.spawner.onEventTriggered(this.level, id) || super.triggerEvent(id, type);
        } else {
            return false;
        }
    }

    @Override
    public float calculateAddedStressCapacity() {
        return generating && entitySize > 0 ? entitySize * super.calculateAddedStressCapacity() : 0;
    }

    @Override
    public float calculateStressApplied() {
        return generating ? 0 : Mth.abs(entitySize) * super.calculateStressApplied();
    }

    @Override
    public float getGeneratedSpeed() {
        return generating && entitySize > 0 ? Config.generationSpeed : 0;
    }

    public void neighborChanged() {
        boolean newGenerating = level != null && level.hasNeighborSignal(worldPosition);
        if (newGenerating != generating) {
            generating = newGenerating;
            updateEntitySize(true);
        } else {
            updateEntitySize(false);
        }
    }
}