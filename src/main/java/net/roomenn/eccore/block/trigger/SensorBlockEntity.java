package net.roomenn.eccore.block.trigger;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.utils.trigger.TriggerLists;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SensorBlockEntity extends BlockEntity {
    public static final EnumProperty<TriggerBlockType> TYPE = SensorBlock.TYPE;
    private static final TriggerBlockType connected = TriggerBlockType.CONNECTED;
    private static final TriggerBlockType over_connected = TriggerBlockType.OVER_CONNECTED;
    private static final TriggerBlockType unconnected = TriggerBlockType.UNCONNECTED;
    public static final Direction[] DIRECTIONS = SensorBlock.getDirections();
    public BlockPos monitorPos;

    public SensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.SENSOR, pos, state);
    }

    /**
     * Propagate the connected and over_connected states
     * Assumes the network isn't faulty (like a not spread over connection)
     */
    public void update() {
        if (world == null) return;

        for(Direction direction: DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if (entity == null) continue;

            BlockState state = getCachedState();
            TriggerBlockType type = state.get(TYPE);

            if (entity instanceof MonitorBlockEntity monitorEntity) {
                if (monitorPos == null) {
                    this.monitorPos = monitorEntity.getPos();
                    this.setState(connected);
                    this.update();
                    return;
                } else if (monitorPos != monitorEntity.getPos() && type != over_connected){
                    this.setState(over_connected);
                    this.update();
                    return;
                }

            } else if (entity instanceof SensorBlockEntity sensorEntity) {
                if (sensorEntity.monitorPos != null) {
                    BlockState sensorState = sensorEntity.getCachedState();
                    TriggerBlockType sensorType = sensorState.get(TYPE);

                    if (monitorPos != null) {
                        if (monitorPos.equals(sensorEntity.monitorPos)) {
                            if (type == over_connected && sensorType != over_connected){
                                //Classic Spread: over_connected
                                sensorEntity.setState(over_connected);
                                sensorEntity.update();
                            }
                        } else if (type != over_connected) {
                            //Creation of over connection
                            this.setState(over_connected);
                            this.update();
                            return;
                        } else if (sensorType != over_connected) {
                            //Classic Spread: over_connected
                            sensorEntity.setState(over_connected);
                            sensorEntity.update();
                        }
                    } else if (sensorType != unconnected) {
                        //monitor == null: new Sensor Block placed
                        /**
                         * TODO Fix residual sensorEntity.monitorPos or residual sensorEntity
                         * Replicate bug: Disconnect a sensor by breaking its adjacent monitor
                         *  and place a new sensor next to it.
                         *  The sensorEntity.monitorPos has been set to null but the code still
                         *  finds a sensorEntity at its pos where it isn't. Thus, without the
                         *  above if condition, sensorEntity tries to propagate its non-
                         *  existing monitor's pos.
                         */

                        this.monitorPos = sensorEntity.monitorPos;
                        this.setState(sensorType);
                        this.update();
                        return;
                    }
                } else if (monitorPos != null) {
                    //sensorEntity.monitorPos == null
                    sensorEntity.setState(type);
                    sensorEntity.monitorPos = this.monitorPos;
                    sensorEntity.update();
                }
            }
        }
    }

    public void setState(TriggerBlockType type) {
        assert world != null;
        world.setBlockState(this.pos, getCachedState().with(TYPE, type), Block.NOTIFY_LISTENERS);
        markDirty();
    }

    public void setMonitorPos(@Nullable BlockPos newPos) {
        this.monitorPos = newPos;
        markDirty();
    }

    /**
     * Assess the different linked networks and re-assign monitors and states properties
     */
    public void breakUpdate() {
        if (world == null) return;
        BlockState state = getCachedState();
        TriggerBlockType type = state.get(TYPE);

        if (type == over_connected || type == connected){
            ArrayList<SensorBlockEntity> fullSensorList = Lists.newArrayList();
            TriggerLists lists;

            for (Direction direction: DIRECTIONS) {
                BlockEntity entity = world.getBlockEntity(pos.offset(direction));
                if (entity == null) continue;

                if (entity instanceof SensorBlockEntity sensorEntity) {
                    if (fullSensorList.contains(entity)) continue;

                    lists = new TriggerLists();
                    lists.sensorList.add(this);
                    lists.sensorList.add(sensorEntity);
                    lists = sensorEntity.getNetwork(lists);

                    lists.sensorList.remove(this);
                    lists.reAssignNetwork();

                    fullSensorList.addAll(lists.sensorList);
                }
            }
        }
    }

    public TriggerLists getNetwork(TriggerLists lists){
        if (world == null) return lists;

        for(Direction direction: DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if (entity == null) continue;

            if (entity instanceof MonitorBlockEntity monitorEntity && !lists.monitorList.contains(entity)) {
                lists.monitorList.add(monitorEntity);

            } else if (entity instanceof SensorBlockEntity sensorEntity && !lists.sensorList.contains(entity)) {
                lists.sensorList.add(sensorEntity);
                lists = sensorEntity.getNetwork(lists);
            }
        }
        return lists;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtList nbtList = new NbtList();
        if (monitorPos != null) {
            nbtList.add(createPosCompound("x", monitorPos.getX()));
            nbtList.add(createPosCompound("y", monitorPos.getY()));
            nbtList.add(createPosCompound("z", monitorPos.getZ()));
        }
        nbt.put("monitorPos", nbtList);
    }

    private static NbtCompound createPosCompound(String key, int value) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt(key, value);
        return nbtCompound;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList nbtList = nbt.getList("LogList", 10);

        if (!nbtList.isEmpty()) {
            monitorPos = new BlockPos(
                    nbtList.getCompound(0).getInt("x"),
                    nbtList.getCompound(1).getInt("y"),
                    nbtList.getCompound(2).getInt("z"));
        }

        super.readNbt(nbt);
    }


}
