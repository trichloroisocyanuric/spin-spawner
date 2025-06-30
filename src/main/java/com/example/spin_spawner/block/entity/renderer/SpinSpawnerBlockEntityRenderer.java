package com.example.spin_spawner.block.entity.renderer;

import com.example.spin_spawner.block.entity.SpinSpawnerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.simibubi.create.content.kinetics.base.KineticBlockEntity.convertToAngular;

@OnlyIn(Dist.CLIENT)
public class SpinSpawnerBlockEntityRenderer extends KineticBlockEntityRenderer<SpinSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public SpinSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
        entityRenderer = context.getEntityRenderer();
    }

    @Override
    protected BlockState getRenderedBlockState(SpinSpawnerBlockEntity be) {
        return AllBlocks.ANDESITE_BARS.getDefaultState();
    }

    @Override
    protected void renderSafe(SpinSpawnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        KineticBlockEntityRenderer.renderRotatingKineticBlock(be, getRenderedBlockState(be), ms, vb, light);


        Level level = be.getLevel();
        if (level != null && be.hasSpawnData()) {
            BaseSpawner spawner = be.getSpawner();
            Entity entity = spawner.getOrCreateDisplayEntity(level, be.getBlockPos());
            if (entity != null) {
                ms.pushPose();
                ms.translate(0.5F, 0.0F, 0.5F);
                float f = 0.53125F;
                float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
                if ((double) f1 > 1.0) {
                    f /= f1;
                }

                ms.translate(0.0F, 0.4F, 0.0F);
                ms.mulPose(Axis.YP.rotationDegrees((float) spawner.getSpin() + partialTicks * convertToAngular(be.getSpeed())));
                ms.translate(0.0F, -0.2F, 0.0F);
                ms.mulPose(Axis.XP.rotationDegrees(-30.0F));
                ms.scale(f, f, f);
                entityRenderer.render(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks, ms, buffer, light);
                ms.popPose();

            }
        }
    }
}
