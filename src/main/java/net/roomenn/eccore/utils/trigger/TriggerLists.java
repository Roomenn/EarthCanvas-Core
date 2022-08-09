package net.roomenn.eccore.utils.trigger;

import net.minecraft.util.math.BlockPos;
import net.roomenn.eccore.block.abstractBlock.MonitorBlockEntity;
import net.roomenn.eccore.block.trigger.SensorBlockEntity;
import net.roomenn.eccore.block.trigger.TriggerBlockType;

import java.util.ArrayList;

public class TriggerLists {
    public ArrayList<SensorBlockEntity> sensorList = new ArrayList();
    public ArrayList<MonitorBlockEntity> monitorList = new ArrayList();

    public void setSensorState(TriggerBlockType type) {
        for (SensorBlockEntity sensor: sensorList) {
            sensor.setState(type);
        }
    }
    public void setSensorMonitor(BlockPos monitorPos) {
        for (SensorBlockEntity sensor: sensorList) {
            sensor.monitorPos = monitorPos;
        }
    }

    public void reAssignNetwork() {
        if (monitorList.size() == 0) {
            setSensorState(TriggerBlockType.UNCONNECTED);
            setSensorMonitor(null);
        } else if (monitorList.size() == 1) {
            setSensorState(TriggerBlockType.CONNECTED);
            setSensorMonitor(monitorList.get(0).getPos());
        } else {
            setSensorState(TriggerBlockType.OVER_CONNECTED);
            setSensorMonitor(monitorList.get(0).getPos());
        }
    }
}
