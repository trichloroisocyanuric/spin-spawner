package com.example.spin_spawner.block;

import com.example.spin_spawner.block.entity.ModBlockEntities;
import com.example.spin_spawner.block.entity.SpinSpawnerBlockEntity;
import com.example.spin_spawner.data.config.Config;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpinSpawnerBlock extends KineticBlock implements IBE<SpinSpawnerBlockEntity> {
    public SpinSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<SpinSpawnerBlockEntity> getBlockEntityClass() {
        return SpinSpawnerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpinSpawnerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SPIN_SPAWNER.get();
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return Direction.Axis.Y;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    public SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.of(Config.minRpm);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context,
                                @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        Spawner.appendHoverText(stack, tooltipComponents, "SpawnData");
    }

    @Override
    protected @Nonnull List<ItemStack> getDrops(@Nullable BlockState state, LootParams.Builder params) {
        ItemStack itemStack = new ItemStack(this);
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof SpinSpawnerBlockEntity) {
            be.saveToItem(itemStack, params.getLevel().registryAccess());
        }
        return List.of(itemStack);
    }

    @Override
    protected @Nonnull ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, Level level,
                                                       @Nonnull BlockPos pos, @Nonnull Player player,
                                                       @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult
    ) {
        var be = level.getBlockEntity(pos, getBlockEntityType());
        if (be.isEmpty()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        var res = be.get().useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (res == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        return res;
    }

    @Override
    protected boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(id, param);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        withBlockEntityDo(level, pos, SpinSpawnerBlockEntity::neighborChanged);
    }

    @Override
    protected void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos,
                                   @Nonnull Block neighborBlock, @Nonnull BlockPos neighborPos, boolean movedByPiston
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        withBlockEntityDo(level, pos, SpinSpawnerBlockEntity::neighborChanged);
    }
}