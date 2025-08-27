package com.example.spin_spawner.mixin;

import com.example.spin_spawner.data.ModTags;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ManualApplicationRecipe.class)
public abstract class ManualApplicationRecipeMixin {
    @Inject(method = "manualApplicationRecipesApplyInWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;Z)Z", shift = At.Shift.BEFORE)
    )
    private static void beforeDestroyBlock(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci, @Local Level level,
                                           @Local BlockPos pos, @Local ManualApplicationRecipe recipe,
                                           @Share("beStack") LocalRef<ItemStack> itemWithBlockEntityData) {
        Item output = recipe.getRollableResultsAsItemStacks().getFirst().getItem();
        if (output instanceof BlockItem bi) {
            if (bi.getBlock().defaultBlockState().is(ModTags.Blocks.INHERIT_BLOCK_ENTITY_DATA)) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity != null) {
                    ItemStack stack = new ItemStack(output);
                    blockEntity.saveToItem(stack, level.registryAccess());
                    itemWithBlockEntityData.set(stack);
                }
            }
        }
    }

    @Inject(method = "manualApplicationRecipesApplyInWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", shift = At.Shift.AFTER)
    )
    private static void aftersetBlock(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci, @Local Level level,
                                      @Local BlockPos pos, @Share("beStack") LocalRef<ItemStack> itemWithBlockEntityData) {
        ItemStack stack = itemWithBlockEntityData.get();
        if (stack != null) {
            BlockItem.updateCustomBlockEntityTag(level, null, pos, stack);
        }
    }
}
