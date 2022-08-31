package net.roomenn.eccore.block.abstractBlock;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.roomenn.eccore.block.trigger.SensorBlockEntity;
import net.roomenn.eccore.utils.trigger.TriggerLists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.roomenn.eccore.block.trigger.SensorBlockEntity.DIRECTIONS;

public abstract class MonitorBlockEntity extends BlockEntity {
    protected static final Box BOX = new Box(BlockPos.ORIGIN);
    public static final BooleanProperty POWERED = Properties.POWERED;
    public static final int checkingTick = 20; //TODO PUT IN CONFIG

    protected final List<TriggerLog> logList = Lists.newArrayList();
    protected LogType logType;
    protected int poweredTick;
    private boolean isSensor;

    public MonitorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        logType = LogType.ONE_TIME_LOG;
        isSensor = true;
    }

    /* MONITOR BEHAVIOUR */

    /**
     * Override trigger methods to give your monitor a custom behaviour
     */
    protected void trigger(List<Entity> list) {
        list.forEach(entity -> {
            entity.sendMessage(Text.literal("Monitor activated !"));
        });
    }

    public void handleTrigger(@Nullable Entity entity, boolean sensor) {
        if (entity instanceof ServerPlayerEntity player) {
            UUID uuid = player.getUuid();
            refreshLogs(10);
            if (isNotLogged(uuid)) {
                addLog(uuid);

                trigger(Lists.newArrayList(player));
            }
        }
    }

    public void selfTrigger(Entity entity) {
        if (isSensor) handleTrigger(entity, false);
    }

    public void sensorTrigger(Entity entity) {
        handleTrigger(entity, true);
    }

    public void setSensor(boolean bl) {
        this.isSensor = bl;
    }
    public  boolean getSensor() { return isSensor; }


    /* TRIGGER BEHAVIOUR */

    /**
     * On placed, update neighbouring sensor networks
     */
    public void update() {
        if (world == null) return;

        for(Direction direction: DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));

            if (entity instanceof SensorBlockEntity sensorEntity) {
                sensorEntity.update();
            }
        }
    }

    /**
     * Assess the different linked networks and re-assign monitors and states properties
     */
    public void breakUpdate() {
        if (world == null) return;
        List<SensorBlockEntity> fullSensorList = Lists.newArrayList();
        TriggerLists lists;

        for (Direction direction: DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));

            if (entity instanceof SensorBlockEntity sensorEntity) {
                if (fullSensorList.contains(entity)) continue;

                lists = new TriggerLists();
                lists.monitorList.add(this);
                lists.sensorList.add(sensorEntity);
                lists = sensorEntity.getNetwork(lists);

                lists.monitorList.remove(this);
                lists.reAssignNetwork();

                fullSensorList.addAll(lists.sensorList);
            }
        }
    }

    /* PLAYER LOGS */

    public static enum LogType {
        NO_LOG,
        ONE_TIME_LOG,
        TIMEOUT_LOG;

        private LogType() {
        }
    }

    public void setLogType(LogType type) {
        logType = type;
    }

    public LogType getLogType(int index) {
        return switch (index) {
            case 0 -> LogType.NO_LOG;
            case 2 -> LogType.TIMEOUT_LOG;
            default -> LogType.ONE_TIME_LOG;
        };
    }

    public void addLog(UUID uuid) {
        logList.add(new TriggerLog(uuid.toString()));
        markDirty();
    }

    public void flushLogs() {
        logList.clear();
    }

    public boolean isNotLogged(UUID uuid) {
        String id = uuid.toString();
        for (TriggerLog log:logList) {
            if (Objects.equals(log.uuid, id)) return false;
        }
        return true;
    }

    public void refreshLogs(int timeout){
        List<TriggerLog> logsToRemove = Lists.newArrayList();
        long now = System.currentTimeMillis();
        timeout = timeout*1000;
        for (TriggerLog log: logList) {
            if (now - log.timestamp > timeout) {
                logsToRemove.add(log);
            }
        }
        logList.removeAll(logsToRemove);
    }

    public static class TriggerLog {
        public final String uuid;
        public final long timestamp;

        public TriggerLog(String uuid) {
            this.uuid = uuid;
            timestamp = System.currentTimeMillis();
        }

        // Reading Logs
        public TriggerLog(String uuid, Long timestamp) {
            this.uuid = uuid;
            this.timestamp = timestamp;
        }
    }


    /* SAVING */

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList nbtList = new NbtList();

        for(TriggerLog log: logList) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Uuid", log.uuid);
            nbtCompound.putLong("Timestamp", log.timestamp);
            nbtList.add(nbtCompound);
        }

        if (!nbtList.isEmpty()) {
            nbt.put("LogList", nbtList);
        }
        nbt.putBoolean("isSensor", isSensor);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        setSensor(nbt.getBoolean("isSensor"));
        NbtList nbtList = nbt.getList("LogList", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            logList.add(new TriggerLog(
                    nbtCompound.getString("Uuid"),
                    nbtCompound.getLong("Timestamp")));
        }
        super.readNbt(nbt);
    }

    /* BLOCK STATE PROPERTY */

    public boolean getPowered() {
        return getCachedState().get(POWERED);
    }

    public void setPowered(boolean bl) {
        if (world != null)
            world.setBlockState(this.pos, getCachedState().with(POWERED, bl), Block.NOTIFY_LISTENERS);
    }
}
