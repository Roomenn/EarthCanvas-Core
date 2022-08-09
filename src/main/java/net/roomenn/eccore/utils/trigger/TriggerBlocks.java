package net.roomenn.eccore.utils.trigger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.roomenn.eccore.block.ModBlocks;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;

import java.util.ArrayList;
import java.util.Set;

public class TriggerBlocks {

    private static final Set<Block> TRIGGER_BLOCKS;

    static {
        TRIGGER_BLOCKS = Set.of(
                ModBlocks.CUTSCENE_MONITOR,
                ModBlocks.SENSOR_BLOCK);
    }

    public static Set<Block> getTriggerBlocks() {
        return TRIGGER_BLOCKS;
    }



}
