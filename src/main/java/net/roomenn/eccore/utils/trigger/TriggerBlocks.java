package net.roomenn.eccore.utils.trigger;

import net.minecraft.block.Block;
import net.roomenn.eccore.block.ModBlocks;

import java.util.Set;

public class TriggerBlocks {

    private static final Set<Block> TRIGGER_BLOCKS;

    static {
        TRIGGER_BLOCKS = Set.of(
                ModBlocks.CUTSCENE_MONITOR,
                ModBlocks.REDSTONE_MONITOR,
                ModBlocks.SENSOR_BLOCK);
    }

    public static Set<Block> getTriggerBlocks() {
        return TRIGGER_BLOCKS;
    }



}
