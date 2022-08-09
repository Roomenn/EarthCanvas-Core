package net.roomenn.eccore.utils.cameraAnimation;

import net.minecraft.util.StringIdentifiable;

public enum CutSceneBlockType implements StringIdentifiable {
    TYPE_MAIN("main"),
    TYPE_SENSOR("sensor"),
    TYPE_NODE("node");

    private final String name;

    CutSceneBlockType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
