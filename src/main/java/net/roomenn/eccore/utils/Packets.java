package net.roomenn.eccore.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import net.roomenn.eccore.utils.cameraAnimation.CameraAnimation;
import net.roomenn.eccore.utils.cameraAnimation.ICamera;

public class Packets {
    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.CAMERA_ANIMATION_PACKET_ID, (client, handler, buf, responseSender) -> {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            Float time = buf.readFloat();
            String type = buf.readString();
            boolean hold = buf.readBoolean();
            client.execute(() -> {
                Camera cam =  client.gameRenderer.getCamera();
                if (cam.isThirdPerson()) {
                    ((ICamera) cam).addAnim(
                            new CameraAnimation(cam, new Vec3d(x,y,z), time, type, hold)
                    );
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(Constants.CAMERA_ANIMATION_SKIP_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                Camera cam =  client.gameRenderer.getCamera();
                if (cam.isThirdPerson()) {
                    ((ICamera) cam).skipAnim();
                }
            });
        });
    }
}
