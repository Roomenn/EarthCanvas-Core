package net.roomenn.eccore.utils;

import net.minecraft.util.Identifier;
import net.roomenn.eccore.ECCore;

public class Constants {
    public static final Identifier CAMERA_ANIMATION_PACKET_ID = new Identifier(ECCore.MOD_ID, "camera_animation_packet");
    public static final Identifier CAMERA_ANIMATION_SKIP_PACKET_ID = new Identifier(ECCore.MOD_ID, "camera_animation_skip_packet");

    public static void registerConstants(){}
}