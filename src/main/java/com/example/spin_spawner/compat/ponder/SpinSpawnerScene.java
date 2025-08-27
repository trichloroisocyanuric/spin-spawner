package com.example.spin_spawner.compat.ponder;

import com.example.spin_spawner.block.ModBlocks;
import com.example.spin_spawner.block.entity.SpinSpawnerBlockEntity;
import com.example.spin_spawner.data.config.Config;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.element.TextElementBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class SpinSpawnerScene {
    private static void setSpawnerEntity(CreateSceneBuilder.WorldInstructions world, BlockPos pos, EntityType<?> entity) {
        world.modifyBlockEntity(pos, SpinSpawnerBlockEntity.class, (be) ->
                be.setEntityId(entity, be.getLevel() != null ? be.getLevel().random : null)
        );
    }

    private static BlockPos initSpawnerBlock(CreateSceneBuilder scene, SceneBuildingUtil util, int y) {
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos spawnerPos = util.grid().at(2, y, 2);
        scene.world().setBlock(spawnerPos, ModBlocks.SPIN_SPAWNER.getDefaultState(), false);
        setSpawnerEntity(scene.world(), spawnerPos, EntityType.PIG);
        scene.idle(20);

        scene.world().showSection(util.select().position(spawnerPos), Direction.DOWN);

        scene.overlay().showText(20)
                .pointAt(util.vector().topOf(spawnerPos))
                .placeNearTarget()
                .sharedText("this_is")
                .attachKeyFrame();

        scene.idle(30);
        return spawnerPos;
    }

    public static void spawner(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("spin_spawner_spawner", "Spin Spawner Spawner Mode");

        BlockPos spawnerPos = initSpawnerBlock(scene, util, 1);

        BlockPos shaftPos = spawnerPos.above();
        scene.world().setBlock(shaftPos, AllBlocks.SHAFT.getDefaultState(), false);
        scene.world().setKineticSpeed(util.select().position(shaftPos), Config.minRpm);
        scene.world().showSection(util.select().position(shaftPos), Direction.DOWN);

        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(spawnerPos), Config.minRpm);
        scene.effects().rotationDirectionIndicator(shaftPos);
        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(shaftPos))
                .placeNearTarget()
                .text("It can ignore spawning conditions except empty space.");

        scene.idle(40);


        ElementLink<EntityElement> pig = scene.world().createEntity(world -> {
            Entity entity = EntityType.PIG.create(world);
            if (entity != null) {
                entity.setPos(spawnerPos.getX() - 1.5, spawnerPos.getY(), spawnerPos.getZ() + 1.5);
            }
            return entity;
        });
        scene.idle(30);
        scene.world().modifyEntity(pig, (entity) -> entity.remove(Entity.RemovalReason.DISCARDED));
        scene.idle(10);

        scene.addKeyframe();
        scene.overlay().showControls(util.vector().topOf(spawnerPos), Pointing.DOWN, 20)
                .rightClick().withItem(new ItemStack(Items.GHAST_SPAWN_EGG));
        scene.idle(20);

        setSpawnerEntity(scene.world(), spawnerPos, EntityType.GHAST);
        scene.effects().emitParticles(util.vector().topOf(spawnerPos),
                (l, x, y, z) -> {
                    Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, l.random, 0.2f);
                    l.addParticle(ParticleTypes.SMOKE, x, y, z, motion.x, motion.y, motion.z);
                },
                40, 4);
        scene.world().setKineticSpeed(util.select().everywhere(), 0.f);

        sharedText(scene.overlay().showText(60)
                        .pointAt(util.vector().topOf(spawnerPos))
                        .colored(PonderPalette.RED)
                        .placeNearTarget(),
                I18n.get("create.gui.stressometer.overstressed")
        );

        scene.overlay().showText(60)
                .pointAt(util.vector().topOf(shaftPos))
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .text("Required stress is proportional to the sqrt of the volume of entity.");

        scene.idle(70);
    }

    public static void generator(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);

        scene.title("spin_spawner_generator", "Spin Spawner Generator Mode");

        BlockPos spawnerPos = initSpawnerBlock(scene, util, 2);

        BlockPos redstonePos = spawnerPos.east();
        scene.world().setBlock(redstonePos, Blocks.REDSTONE_BLOCK.defaultBlockState(), true);
        scene.world().showSection(util.select().position(redstonePos), Direction.DOWN);

        scene.overlay().showText(60)
                .pointAt(util.vector().centerOf(redstonePos))
                .placeNearTarget()
                .text("When it has redstone signal, instead of spawning entities, it will generate stress.");
        scene.idle(70);

        scene.addKeyframe();
        BlockPos grassPos = spawnerPos.below();
        scene.world().setBlock(grassPos, Blocks.GRASS_BLOCK.defaultBlockState(), false);
        scene.world().showSection(util.select().position(grassPos), Direction.UP);
        BlockPos torchPos = spawnerPos.west();

        scene.world().setBlock(torchPos, Blocks.JACK_O_LANTERN.defaultBlockState(), true);
        scene.world().showSection(util.select().position(torchPos), Direction.DOWN);

        scene.idle(10);

        scene.world().modifyBlockEntity(spawnerPos, SpinSpawnerBlockEntity.class, (be) -> {
            be.fakeGenerating();
            be.setSpeed(64);
        });

        BlockPos shaftPos = spawnerPos.above();
        scene.world().setBlock(shaftPos, AllBlocks.SHAFT.getDefaultState(), false);
        scene.world().showSection(util.select().position(shaftPos), Direction.DOWN);

        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(shaftPos), 64);
        scene.effects().rotationSpeedIndicator(shaftPos);

        sharedText(scene.overlay().showText(80)
                        .pointAt(util.vector().centerOf(grassPos))
                        .placeNearTarget(),
                I18n.get("create.gui.spawner.incorrect_spawn_rule_1")
        );
        scene.idle(90);

        sharedText(scene.overlay().showText(80)
                .pointAt(util.vector().centerOf(spawnerPos))
                .placeNearTarget(), I18n.get("create.gui.spawner.incorrect_spawn_rule_2"));
        scene.idle(90);

        text(scene.overlay().showText(60)
                        .pointAt(util.vector().topOf(shaftPos))
                        .placeNearTarget(),
                "This mode has a fixed speed of %1$d rpm and the same stress as the spawner mode.", Config.generationSpeed
        );

        scene.idle(70);
    }

    private static void text(TextElementBuilder builder, String text, Object... params) {
        try {
            builder.text(text, params);
        } catch (NoSuchMethodError ignored) {
            builder.text(text);
        }
    }

    private static void sharedText(TextElementBuilder builder, Object... params) {
        try {
            builder.sharedText("see_other", params);
        } catch (NoSuchMethodError ignored) {
            builder.sharedText("upgrade_ponder");
        }
    }
}