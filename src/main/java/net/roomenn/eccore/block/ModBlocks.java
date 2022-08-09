package net.roomenn.eccore.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.trigger.CutSceneBlock;
import net.roomenn.eccore.block.trigger.SensorBlock;
import net.roomenn.eccore.item.trigger.TriggerBlockItem;

public class ModBlocks {
    public static final Block CUTSCENE_MONITOR = registerTriggerBlock("cutscene_block",
            new CutSceneBlock(FabricBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque()),
            ItemGroup.MISC);
    public static final Block SENSOR_BLOCK = registerTriggerBlock("sensor_block",
            new SensorBlock(FabricBlockSettings.of(Material.AIR).strength(-1.0F, 3600000.8F).dropsNothing().nonOpaque()),
            ItemGroup.MISC);


    private static Block registerTriggerBlock(String name, Block block, ItemGroup tab) {
        registerTriggerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(ECCore.MOD_ID, name), block);
    }

    private static void registerTriggerBlockItem(String name, Block block, ItemGroup tab) {
        Registry.register(Registry.ITEM, new Identifier(ECCore.MOD_ID, name),
                new TriggerBlockItem(block, new FabricItemSettings().group(tab)));
    }



    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(ECCore.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block, ItemGroup tab) {
        Registry.register(Registry.ITEM, new Identifier(ECCore.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }

    public static void registerModBlocks() {

    }
}
