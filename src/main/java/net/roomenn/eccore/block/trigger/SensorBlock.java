package net.roomenn.eccore.block.trigger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.block.abstractBlock.TriggerBlock;
import org.jetbrains.annotations.Nullable;

public class SensorBlock extends TriggerBlock {
    public static final EnumProperty<TriggerBlockType> TYPE = EnumProperty.of("type", TriggerBlockType.class);

    public SensorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, TriggerBlockType.UNCONNECTED));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        SensorBlockEntity sensorEntity = (SensorBlockEntity) world.getBlockEntity(pos);
        if (sensorEntity != null && sensorEntity.getCachedState().get(TYPE) == TriggerBlockType.CONNECTED) {
            if (sensorEntity.monitorPos == null) return;
            MonitorBlockEntity monitorEntity = (MonitorBlockEntity) world.getBlockEntity(sensorEntity.monitorPos);
            if (monitorEntity != null) monitorEntity.handleTrigger(entity);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ((SensorBlockEntity) world.getBlockEntity(pos)).update();
    }

    public static Direction[] getDirections() {
        return DIRECTIONS;
    }

    /* BLOCK ENTITY */

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SensorBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SensorBlockEntity sensorEntity) {
                sensorEntity.breakUpdate();

                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
