package net.roomenn.eccore.block.trigger;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.block.ModBlockEntity;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.utils.trigger.TriggerLists;

import java.util.ArrayList;

public class SensorBlockEntity extends BlockEntity {
    public static final EnumProperty<TriggerBlockType> TYPE = SensorBlock.TYPE;
    private static final TriggerBlockType connected = TriggerBlockType.CONNECTED;
    private static final TriggerBlockType over_connected = TriggerBlockType.OVER_CONNECTED;
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
        BlockEntity monitor= (monitorPos == null) ? null : world.getBlockEntity(monitorPos);

        for(Direction direction: DIRECTIONS) {
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if (entity == null) continue;

            BlockState blockState = entity.getCachedState();
            BlockState state = getCachedState();

            if (entity instanceof MonitorBlockEntity monitorEntity) {
                if (monitor == null) {
                    this.monitorPos = monitorEntity.getPos();
                    this.setState(connected);
                    this.update();
                    return;
                } else if (monitor != monitorEntity && state.get(TYPE) != over_connected){
                    this.setState(over_connected);
                    this.update();
                    return;
                }

            } else if (entity instanceof SensorBlockEntity sensorEntity) {
                if (sensorEntity.monitorPos != null) {
                    BlockEntity sensorEntityMonitor= world.getBlockEntity(sensorEntity.monitorPos);

                    if (monitor != null) {
                        if (monitor == sensorEntityMonitor) {
                            if (state.get(TYPE) == over_connected && blockState.get(TYPE) != over_connected){
                                //Classic Spread: over_connected
                                sensorEntity.setState(over_connected);
                                sensorEntity.update();
                            }
                        } else if (state.get(TYPE) != over_connected) {
                            //Creation of over connection
                            this.setState(over_connected);
                            this.update();
                            return;
                        }
                    } else {
                        //monitor == null: new Sensor Block placed
                        this.monitorPos = sensorEntityMonitor.getPos();
                        if (blockState.get(TYPE) == over_connected) {
                            this.setState(over_connected);
                        } else {
                            this.setState(connected);
                        }
                        this.update();
                        return;
                    }
                } else if (monitor != null) {
                    //sensorEntityMonitor == null

                    if (state.get(TYPE) == over_connected) {
                        //Classic Spread: over_connected
                        sensorEntity.setState(over_connected);
                    } else {
                        //Classic Spread: connected
                        sensorEntity.setState(connected);
                    }
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

    /**
     * Assess the different linked networks and re-assign monitors and states properties
     */
    public void breakUpdate() {
        if (world == null) return;
        BlockState state = getCachedState();
        TriggerBlockType type = state.get(TYPE);

        if (type == over_connected || type == connected){
            ArrayList<SensorBlockEntity> fullSensorList = new ArrayList();
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

        if (monitorPos != null) {
            ECCore.LOGGER.info("Sensor Writing BlockPos: " + monitorPos);
            int[] nbtPos;
            nbtPos = new int[] {monitorPos.getX(), monitorPos.getY(), monitorPos.getZ()};
            nbt.putIntArray("monitorPos", nbtPos);
        }

    }

    @Override
    public void readNbt(NbtCompound nbt) {
        ECCore.LOGGER.info("Sensor Reading NBT");
        int[] posArray = nbt.getIntArray("monitorPos");

        if (posArray != null) {
            monitorPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
        }

        super.readNbt(nbt);
    }


}
