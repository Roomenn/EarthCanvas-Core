package net.roomenn.eccore.block.trigger;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.screen.RedstoneMonitorGuiDescription;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RedstoneMonitorBlockEntity extends MonitorBlockEntity implements NamedScreenHandlerFactory {
    protected TriggerType triggerType;
    private int[] settingArray;


    public RedstoneMonitorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.REDSTONE_MONITOR, pos, state);
        // settingArray: {logType, logTimeout, triggerType, tickRate, targetPlayers, targetMobs, targetItems}
        settingArray = new int[]{1, 0, 0, 0, 1, 0, 0};
        triggerType = TriggerType.HOLD;
        markDirty();
    }

    /* BLOCK ENTITY */

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putIntArray("settingArray", settingArray);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setSettingArray(nbt.getIntArray("settingArray"));
        super.readNbt(nbt);
    }

    public static void tick(World world, BlockPos pos, BlockState state, RedstoneMonitorBlockEntity monitor) {
        if (--monitor.poweredTick == 0) {
            monitor.handleTrigger(null, false);
        }
    }


    /* SCREEN HANDLER FACTORY */

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new RedstoneMonitorGuiDescription(syncId, inv, pos);

    }
    /* MONITOR BEHAVIOUR */

    public int[] getSettingArray() {
        return settingArray;
    }

    public void setSettingArray(int[] array) {
        settingArray = array;
        setLogType(getLogType(settingArray[0]));
        setTriggerType(getTriggerType(settingArray[2]));
        if (settingArray[1] < 1) settingArray[1] = 1;
        if (settingArray[3] < checkingTick) settingArray[3] = checkingTick;
        markDirty();
    }

    @Override
    public void handleTrigger(@Nullable Entity entity, boolean sensor) {
        List<Entity> list = getTriggeringEntities(world, pos);
        boolean powered = getPowered();
        boolean triggered = !list.isEmpty() || sensor;

        if (triggered) {
            if (sensor) {
                if (list.isEmpty()) list = Lists.newArrayList(entity);
                else list.add(entity);
            }
            trigger(list);
            setPowered(true);

            if (triggerType == TriggerType.TIMED) {
                poweredTick = settingArray[3];
            } else {
                poweredTick = checkingTick;
            }

        } else if (powered) {
            if (triggerType == TriggerType.HOLD_TIMED) poweredTick = settingArray[3];
            else trigger(list);

            setPowered(false);
        } else if (entity == null) {
            trigger(list);
        }
    }

    @Override
    public void sensorTrigger(Entity entity) {
        if (!getCachedState().get(POWERED)) {
            if (entity instanceof PlayerEntity && settingArray[4] == 1) handleTrigger(entity, true);
            else if (entity instanceof MobEntity && settingArray[5] == 1) handleTrigger(entity, true);
            else if (entity instanceof ItemEntity && settingArray[6] == 1) handleTrigger(entity, true);
        }
    }

    protected List<Entity> getTriggeringEntities(World world, BlockPos pos) {
        Box box = BOX.offset(pos);
        List<Entity> list = Lists.newArrayList();

        if (settingArray[4] == 1) list.addAll(world.getNonSpectatingEntities(PlayerEntity.class, box));
        else if (settingArray[5] == 1) list.addAll(world.getNonSpectatingEntities(MobEntity.class, box));
        else if (settingArray[6] == 1) list.addAll(world.getNonSpectatingEntities(ItemEntity.class, box));


        switch (logType) {
            case TIMEOUT_LOG:
                refreshLogs(settingArray[1]);
            case ONE_TIME_LOG:
                List<Entity> list2 = list.stream().filter(entity -> isNotLogged(entity.getUuid())).toList();
                list2.forEach(entity -> addLog(entity.getUuid()));
                return list2;
            default:
                return list;
        }
    }

    /* TRIGGER TYPE */

    public enum TriggerType {
        HOLD,
        TIMED,
        HOLD_TIMED;

        TriggerType() {}
    }

    public void setTriggerType(TriggerType type) {
        triggerType = type;
    }

    public TriggerType getTriggerType(int index) {
        return switch (index) {
            case 1 -> TriggerType.TIMED;
            case 2 -> TriggerType.HOLD_TIMED;
            default -> TriggerType.HOLD;
        };
    }

}
