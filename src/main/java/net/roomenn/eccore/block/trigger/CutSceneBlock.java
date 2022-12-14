package net.roomenn.eccore.block.trigger;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlock;
import org.jetbrains.annotations.Nullable;

public class CutSceneBlock extends MonitorBlock implements BlockEntityProvider {

    public CutSceneBlock(Settings settings) {
        super(settings);
    }

    /* BLOCK ENTITY */


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CutSceneBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntity.CUTSCENE_MONITOR, CutSceneBlockEntity::tick);
    }
}

