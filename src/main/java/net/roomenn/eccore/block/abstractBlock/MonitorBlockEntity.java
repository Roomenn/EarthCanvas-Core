package net.roomenn.eccore.block.abstractBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.trigger.SensorBlockEntity;
import net.roomenn.eccore.utils.trigger.TriggerLists;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import static net.roomenn.eccore.block.trigger.SensorBlockEntity.DIRECTIONS;

public abstract class MonitorBlockEntity extends BlockEntity {
    private final ArrayList<TriggerLog> logList = new ArrayList();
    private boolean isSensor;

    public MonitorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        isSensor = true;
    }

    /* MONITOR BEHAVIOUR */

    public void handleTrigger(Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            UUID uuid = player.getUuid();
            if (!isLogged(uuid)) {
                addLog(uuid);

                //TODO
                player.sendMessage(Text.literal("Monitor check !"));
            }
        }
    }

    public void selfTrigger(Entity entity) {
        if (isSensor) handleTrigger(entity);
    }


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
        ArrayList<SensorBlockEntity> fullSensorList = new ArrayList();
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

    public void addLog(UUID uuid) {
        logList.add(new TriggerLog(uuid.toString()));
        markDirty();
    }

    public boolean isLogged(UUID uuid) {
        String id = uuid.toString();
        for (TriggerLog log:logList) {
            if (Objects.equals(log.uuid, id)) return true;
        }
        return false;
    }

    public static class TriggerLog {
        public final String uuid;

        public TriggerLog(String uuid) {
            this.uuid = uuid;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        NbtList nbtList = new NbtList();

        for(TriggerLog log: logList) {
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putString("Uuid", log.uuid);
            nbtList.add(nbtCompound);
        }

        if (!nbtList.isEmpty()) {
            nbt.put("LogList", nbtList);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = nbt.getList("LogList", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            logList.add(new TriggerLog(nbtCompound.getString("Uuid")));
        }
        super.readNbt(nbt);
    }
}
