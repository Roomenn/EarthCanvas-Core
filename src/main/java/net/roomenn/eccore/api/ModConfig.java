package net.roomenn.eccore.api;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.roomenn.eccore.ECCore;

@Config(name = ECCore.MOD_ID)
public class ModConfig implements ConfigData {

    public String transition = "linear";

    public float thirdPersonOffsetX = -5;
    public float thirdPersonOffsetY = 0;
    public float thirdPersonOffsetZ = 0;

    public float camAnimX = -8;
    public float camAnimY = -1.3f;
    public float camAnimZ = 2;

}