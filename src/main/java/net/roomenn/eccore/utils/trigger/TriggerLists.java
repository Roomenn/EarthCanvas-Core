package net.roomenn.eccore.utils.trigger;

import net.minecraft.util.math.BlockPos;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.block.trigger.SensorBlockEntity;
import net.roomenn.eccore.block.trigger.TriggerBlockType;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class TriggerLists {
    public ArrayList<SensorBlockEntity> sensorList = Lists.newArrayList();
    public ArrayList<MonitorBlockEntity> monitorList = Lists.newArrayList();

    public void setSensorMonitor(@Nullable BlockPos monitorPos) {
        for (SensorBlockEntity sensor: sensorList) {
            sensor.setMonitorPos(monitorPos);
        }
    }
    public void setSensorState(TriggerBlockType type) {
        for (SensorBlockEntity sensor: sensorList) {
            sensor.setState(type);
        }
    }

    public void reAssignNetwork() {
        if (monitorList.size() == 0) {
            setSensorMonitor(null);
            setSensorState(TriggerBlockType.UNCONNECTED);
        } else if (monitorList.size() == 1) {
            setSensorMonitor(monitorList.get(0).getPos());
            setSensorState(TriggerBlockType.CONNECTED);
        } else {
            setSensorMonitor(monitorList.get(0).getPos());
            setSensorState(TriggerBlockType.OVER_CONNECTED);
        }
    }
}
