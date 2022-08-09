package net.roomenn.eccore.block.trigger;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.screen.CutSceneMonitorGuiDescription;
import net.roomenn.eccore.utils.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

public class CutSceneBlockEntity extends MonitorBlockEntity implements NamedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(4, ItemStack.EMPTY);

    public CutSceneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.CUTSCENE_MONITOR, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CutSceneMonitorGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, inventory);
        super.readNbt(nbt);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CutSceneBlockEntity entity) {}

}
