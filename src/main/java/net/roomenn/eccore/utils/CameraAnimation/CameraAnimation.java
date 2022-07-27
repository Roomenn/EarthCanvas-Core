package net.roomenn.eccore.utils.CameraAnimation;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.render.Camera;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.roomenn.eccore.utils.Constants;

public class CameraAnimation {
    public final Vec3d paramInit;
    public final float duration;
    public String type;
    public boolean hold;
    public float ticksLeft;
    public final Vec3d vec;
    public FloatArrayList param;

    /**
     * Create a new instance of CameraAnimation
     * <ul><li>relativePosition is a Vec3d player to camera in the world base
     * <li>v is relativePosition in the player base (X'=P'X) with:
     * <ul><li> x = -z
     * <li> z = x</ul>
     * Parameters thus comply with Camera.move() terminology
     * <li>the influence of the pitch is deduced to find the initial parameters</ul>
     */
    public CameraAnimation(Camera cam, Vec3d relativePosWanted, float duration, String type, boolean hold) {
        Vec3d relativePos = (cam.getPos()).subtract(cam.getFocusedEntity().getEyePos());
        double yaw = cam.getFocusedEntity().getYaw()*Math.PI/180;
        double pitch = cam.getFocusedEntity().getPitch()*Math.PI/180;

        Vec3d v = new Vec3d(
                relativePos.x * Math.sin(yaw) - relativePos.z * Math.cos(yaw),
                relativePos.y,
                relativePos.x * Math.cos(yaw) + relativePos.z * Math.sin(yaw));
        this.paramInit = new Vec3d(
                v.x / Math.cos(pitch),
                v.y - (v.x * Math.tan(pitch)),
                v.z);

        this.vec = relativePosWanted.subtract(paramInit);
        this.duration = duration;
        this.ticksLeft = duration;
        this.type = type;
        this.hold = hold;
    }

    public CameraAnimation(Camera cam, Vec3d relativePosWanted, float duration, FloatArrayList bezierParam) {
        this.paramInit = cam.getPos().subtract(cam.getFocusedEntity().getPos());
        this.vec = cam.getFocusedEntity().getPos().subtract(cam.getPos()).add(relativePosWanted);
        this.duration = duration;
        this.ticksLeft = duration;
        this.param = bezierParam;
    }


    public void decrementTick(float tickDelta) {
        ticksLeft = Math.max(0, ticksLeft - tickDelta);
    }

    public Vec3d move(double yaw) {
        Vec3d v;
        if (type != null) {
            return switch (type) {
                case "linear" -> cubicBezier(0, 0, 1, 1);
                case "ease" -> cubicBezier(.25, .1, .25, 1);
                case "ease-in" -> cubicBezier(.42, 0, 1, 1);
                case "ease-out" -> cubicBezier(0, 0, .58, 1);
                case "ease-in-out" -> cubicBezier(.42, 0, .58, 1);
                default -> new Vec3d(0,0,0);
            };
        } else {
            return cubicBezier(param.getFloat(0), param.getFloat(1),param.getFloat(2),param.getFloat(3));
        }
    }

    private Vec3d cubicBezier(double a, double b, double c, double d) {
        /**
         * Calculate the vector Player-Camera in the player referential
         */
        double t = (duration - ticksLeft)/duration;
        double bezier = a*Math.pow(1-t,3) + 3*b*Math.pow(1-t,2)*t + 3*c*(1-t)*Math.pow(t,3) + d*Math.pow(t,3);
        return paramInit.add(vec.multiply(bezier));
    }

    public static void animationPacket(ServerPlayerEntity user, float x, float y, float z, float seconds, String transition, boolean hold ) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeFloat(seconds*20);
        buf.writeString(transition);
        buf.writeBoolean(hold);

        ServerPlayNetworking.send(user, Constants.CAMERA_ANIMATION_PACKET_ID, buf);
    }

    public static void animationSkipPacket(ServerPlayerEntity user) {
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(user, Constants.CAMERA_ANIMATION_SKIP_PACKET_ID, buf);
    }

}
