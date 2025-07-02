package com.example.spin_spawner;

import com.example.spin_spawner.data.config.Config;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

import static com.example.spin_spawner.SpinSpawnerMod.LOGGER;
import static net.neoforged.neoforge.event.EventHooks.finalizeMobSpawnSpawner;

public abstract class BaseSpinSpawner extends BaseSpawner {
    public float spawnDelay = (float) super.spawnDelay;
    public Result lastResult = Result.SUCCEED;

    private void delay(Level level, BlockPos pos, float delay) {
        RandomSource randomsource = level.random;
        this.spawnDelay = delay == 0.f ? (maxSpawnDelay > minSpawnDelay ?
                (float) minSpawnDelay + randomsource.nextFloat() * (maxSpawnDelay - minSpawnDelay) :
                (float) minSpawnDelay) : delay;

        this.broadcastEvent(level, pos, lastResult.ordinal());
    }

    private void delay(Level level, BlockPos pos) {
        delay(level, pos, 0.f);
    }

    private Result trySpawnEntities(ServerLevel serverLevel, BlockPos pos) {
        Result spawned = Result.INSUFFICIENT_SPACE;

        RandomSource randomsource = serverLevel.getRandom();
        if (nextSpawnData == null) return Result.INVALID_ENTITY;

        CompoundTag compoundtag = nextSpawnData.getEntityToSpawn();
        Optional<EntityType<?>> optional = EntityType.by(compoundtag);
        if (optional.isEmpty()) return Result.INVALID_ENTITY;

        ListTag posList = compoundtag.getList("Pos", 6);
        int posSize = posList.size();

        if (!optional.get().getCategory().isFriendly() && serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
            return Result.DIFFICULTY_PEACEFUL;
        }

        for (int i = 0; i < spawnCount; i++) {
            double x = posSize >= 1
                    ? posList.getDouble(0)
                    : (double) pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnRange + 0.5;
            double y = posSize >= 2 ? posList.getDouble(1) : (double) (pos.getY() + randomsource.nextInt(3) - 1);
            double z = posSize >= 3
                    ? posList.getDouble(2)
                    : (double) pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) spawnRange + 0.5;
            if (serverLevel.noCollision(optional.get().getSpawnAABB(x, y, z))) {
                BlockPos blockpos = BlockPos.containing(x, y, z);

                Entity entity = EntityType.loadEntityRecursive(compoundtag, serverLevel, p -> {
                    p.moveTo(x, y, z, p.getYRot(), p.getXRot());
                    return p;
                });

                if (entity == null || isTooManyNearbyEntities(serverLevel, pos, entity.getClass())) {
                    return Result.TOO_MANY_ENTITIES;
                }

                entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                if (entity instanceof Mob mob) {
                    boolean flag1 = nextSpawnData.getEntityToSpawn().size() == 1 && nextSpawnData.getEntityToSpawn().contains("id", 8);
                    finalizeMobSpawnSpawner(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, this, flag1);
                    nextSpawnData.getEquipment().ifPresent(mob::equip);
                }

                if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                    continue;
                }

                serverLevel.levelEvent(2004, pos, 0);
                serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);

                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                spawned = Result.SUCCEED;
            }
        }

        return spawned;
    }

    private boolean isTooManyNearbyEntities(Level level, BlockPos pos, Class<? extends Entity> entity) {
        return level.getEntities(
                EntityTypeTest.forExactClass(entity),
                new AABB(pos).inflate(spawnRange),
                EntitySelector.NO_SPECTATORS
        ).size() >= maxNearbyEntities;
    }

    public boolean checkSpawnRules(ServerLevel serverLevel, BlockPos blockpos) {
        if (nextSpawnData == null) return false;

        Optional<EntityType<?>> optional = EntityType.by(nextSpawnData.getEntityToSpawn());
        if (optional.isEmpty()) return false;

        if (nextSpawnData.getCustomSpawnRules().isPresent()) {
            if (!optional.get().getCategory().isFriendly() && serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
                return false;
            }
            SpawnData.CustomSpawnRules customspawnrules = nextSpawnData.getCustomSpawnRules().get();
            return customspawnrules.isValidPosition(blockpos, serverLevel);
        } else {
            return SpawnPlacements.checkSpawnRules(optional.get(), serverLevel, MobSpawnType.SPAWNER, blockpos, serverLevel.getRandom());
        }
    }

    public float getEntitySize() {
        if (nextSpawnData != null) {
            Optional<EntityType<?>> optional = EntityType.by(nextSpawnData.getEntityToSpawn());
            if (optional.isPresent()) {
                EntityDimensions dimensions = optional.get().getDimensions();
                return Mth.sqrt(dimensions.height() * dimensions.width() * dimensions.width());
            }
        }
        return 0;
    }

    @Override
    public void clientTick(Level level, BlockPos pos) {
        if (this.displayEntity != null) {
            if (!isGenerating()) {
                RandomSource randomsource = level.getRandom();
                double d0 = (double) pos.getX() + randomsource.nextDouble();
                double d1 = (double) pos.getY() + randomsource.nextDouble();
                double d2 = (double) pos.getZ() + randomsource.nextDouble();
                level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
                level.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
            }

            spin += KineticBlockEntity.convertToAngular(getKineticSpeed());
        }
    }

    @Override
    public void serverTick(ServerLevel serverLevel, BlockPos pos) {
        if (spawnDelay > 0) {
            spawnDelay -= Math.abs(getKineticSpeed()) / Config.baseRpm;
        } else {
            lastResult = trySpawnEntities(serverLevel, pos);
            if (lastResult == Result.INSUFFICIENT_SPACE) {
                delay(serverLevel, pos, 8.f);
            } else {
                delay(serverLevel, pos);
            }
        }
    }

    @Override
    public void setNextSpawnData(@Nullable Level level, @Nonnull BlockPos pos, @Nonnull SpawnData spawnData) {
        super.setNextSpawnData(level, pos, spawnData);
        if (level != null) {
            BlockState blockstate = level.getBlockState(pos);
            level.sendBlockUpdated(pos, blockstate, blockstate, 4);
        }
    }

    public boolean hasSpawnData() {
        return nextSpawnData != null;
    }

    @Override
    public @Nonnull CompoundTag save(CompoundTag tag) {
        tag.putShort("Delay", (short) this.spawnDelay);
        tag.putShort("MinSpawnDelay", (short) this.minSpawnDelay);
        tag.putShort("MaxSpawnDelay", (short) this.maxSpawnDelay);
        tag.putShort("SpawnCount", (short) this.spawnCount);
        tag.putShort("MaxNearbyEntities", (short) this.maxNearbyEntities);
        tag.putShort("SpawnRange", (short) this.spawnRange);
        if (this.nextSpawnData != null) {
            tag.put(
                    "SpawnData",
                    SpawnData.CODEC
                            .encodeStart(NbtOps.INSTANCE, this.nextSpawnData)
                            .getOrThrow(p -> new IllegalStateException("Invalid SpawnData: " + p))
            );
        }

        tag.putByte("LastResult", (byte) lastResult.ordinal());

        return tag;
    }

    @Override
    public void load(Level level, @Nullable BlockPos pos, CompoundTag tag) {
        this.spawnDelay = tag.getShort("Delay");

        if (tag.contains("SpawnData", 10)) {
            SpawnData spawndata = SpawnData.CODEC
                    .parse(NbtOps.INSTANCE, tag.getCompound("SpawnData"))
                    .resultOrPartial(p -> LOGGER.warn("Invalid SpawnData: {}", p))
                    .orElseGet(SpawnData::new);
            this.setNextSpawnData(level, pos, spawndata);
        }

        if (tag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = tag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            this.spawnCount = tag.getShort("SpawnCount");
        }

        if (tag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
        }

        if (tag.contains("SpawnRange", 99)) {
            this.spawnRange = tag.getShort("SpawnRange");
        }

        if (tag.contains("LastResult", 99)) {
            this.lastResult = Result.values()[tag.getByte("LastResult")];
        }

        this.displayEntity = null;
    }

    @Override
    public void setEntityId(EntityType<?> type, @Nullable Level level, RandomSource random, BlockPos pos) {
        super.setEntityId(type, level, random, pos);
        lastResult = Result.SUCCEED;
    }

    @Override
    public boolean onEventTriggered(Level level, int id) {
        if (id < Result.values().length) {
            if (level.isClientSide) {
                this.lastResult = Result.values()[id];
            }
            return true;
        } else {
            return false;
        }
    }

    protected abstract float getKineticSpeed();

    protected abstract boolean isGenerating();

    public enum Result {
        SUCCEED(null),
        INVALID_ENTITY("gui.spawner.invalid_entity"),
        TOO_MANY_ENTITIES("gui.spawner.too_many_entities"),
        INSUFFICIENT_SPACE("gui.spawner.insufficient_space"),
        DIFFICULTY_PEACEFUL("gui.spawner.difficulty_peaceful");

        public final @Nullable String langKey;

        Result(@Nullable String langKey) {
            this.langKey = langKey;
        }
    }
}
