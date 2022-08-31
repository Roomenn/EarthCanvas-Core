package net.roomenn.eccore.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.trigger.CutSceneBlockEntity;
import net.roomenn.eccore.block.trigger.RedstoneMonitorBlockEntity;
import net.roomenn.eccore.block.trigger.SensorBlockEntity;

public class ModBlockEntity {
    public static BlockEntityType<CutSceneBlockEntity> CUTSCENE_MONITOR;
    public static BlockEntityType<RedstoneMonitorBlockEntity> REDSTONE_MONITOR;
    public static BlockEntityType<SensorBlockEntity> SENSOR;

    public static void registerAllBlockEntities() {
        CUTSCENE_MONITOR = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ECCore.MOD_ID, "cutscene_monitor"),
                FabricBlockEntityTypeBuilder.create(CutSceneBlockEntity::new,
                        ModBlocks.CUTSCENE_MONITOR).build(null));
        REDSTONE_MONITOR = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ECCore.MOD_ID, "redstone_monitor"),
                FabricBlockEntityTypeBuilder.create(RedstoneMonitorBlockEntity::new,
                        ModBlocks.REDSTONE_MONITOR).build(null));

        SENSOR = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(ECCore.MOD_ID, "sensor"),
                FabricBlockEntityTypeBuilder.create(SensorBlockEntity::new,
                        ModBlocks.SENSOR_BLOCK).build(null));
    }
}
