package net.roomenn.eccore.block.abstractBlock;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.roomenn.eccore.block.trigger.CutSceneBlockEntity;

public abstract class MonitorBlock extends TriggerBlock {

    public MonitorBlock(Settings settings) {
        super(settings);
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

    /* BLOCK ENTITY */

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MonitorBlockEntity monitorEntity) {
                monitorEntity.breakUpdate();

                ItemScatterer.spawn(world, pos, (CutSceneBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

}
