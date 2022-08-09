package net.roomenn.eccore.block.trigger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlock;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CutSceneBlock extends MonitorBlock implements BlockEntityProvider {
    public static final EnumProperty<TriggerBlockType> TYPE = EnumProperty.of("type", TriggerBlockType.class);
    public boolean isSensor = true;

    public CutSceneBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, TriggerBlockType.UNCONNECTED));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (isSensor && entity instanceof ServerPlayerEntity player) {
            ((MonitorBlockEntity) Objects.requireNonNull(
                    world.getBlockEntity(pos))).selfTrigger(player);
        }
    }

    /* TRIGGER BEHAVIOUR */

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world == null) return;
        ((MonitorBlockEntity) Objects.requireNonNull(
                world.getBlockEntity(pos))).update();
    }

    /* BLOCK ENTITY */

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }

        return ActionResult.SUCCESS;
    }

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

