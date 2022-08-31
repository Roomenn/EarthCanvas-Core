package net.roomenn.eccore.utils;

import net.minecraft.util.Identifier;
import net.roomenn.eccore.ECCore;

public class Constants {
    public static final Identifier CAMERA_ANIMATION_PACKET_ID = new Identifier(ECCore.MOD_ID, "camera_animation_packet");
    public static final Identifier CAMERA_ANIMATION_SKIP_PACKET_ID = new Identifier(ECCore.MOD_ID, "camera_animation_skip_packet");

    public static final Identifier READY_MESSAGE_ID = new Identifier(ECCore.MOD_ID, "monitor.ready");
    public static final Identifier INIT_MESSAGE_ID = new Identifier(ECCore.MOD_ID, "monitor.init");
    public static final Identifier UPDATE_DATA_MESSAGE_ID = new Identifier(ECCore.MOD_ID, "monitor.update_data");
    public static final Identifier FLUSH_LOGS_MESSAGE_ID = new Identifier(ECCore.MOD_ID, "monitor.flush_logs");
    public static final Identifier IS_SENSOR_MESSAGE_ID = new Identifier(ECCore.MOD_ID, "monitor.is_sensor");

    public static void registerConstants(){}
}