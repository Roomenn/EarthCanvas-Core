package net.roomenn.eccore.block.abstractBlock;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.roomenn.eccore.utils.trigger.TriggerBlocks;

public abstract class TriggerBlock extends BlockWithEntity {
    public TriggerBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {

        for (Block block: TriggerBlocks.getTriggerBlocks()) {
            if (context.isHolding(block.asItem())) return VoxelShapes.fullCube();
        }
        return VoxelShapes.empty();
    }

    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }
}
