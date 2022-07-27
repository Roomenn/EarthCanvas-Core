package net.roomenn.eccore.mixin;

import net.minecraft.client.render.Camera;
import net.roomenn.eccore.utils.CameraAnimation.ICamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface ICameraMixin extends ICamera {

    @Invoker("setPos")
    void invokeSetPos(double x, double y, double z);
    @Invoker("moveBy")
    void invokeMoveBy(double x, double y, double z);
    @Invoker("clipToSpace")
    double invokeClipToSpace(double desiredCameraDistance);

}
