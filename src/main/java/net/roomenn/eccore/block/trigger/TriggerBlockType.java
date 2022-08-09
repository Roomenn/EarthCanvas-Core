package net.roomenn.eccore.block.trigger;

import net.minecraft.util.StringIdentifiable;

public enum TriggerBlockType implements StringIdentifiable {
    CONNECTED("connected"),
    UNCONNECTED("unconnected"),
    OVER_CONNECTED("over_connected");

    private final String name;

    TriggerBlockType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
