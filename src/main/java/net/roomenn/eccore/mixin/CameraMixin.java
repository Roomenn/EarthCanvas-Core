package net.roomenn.eccore.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.api.ModConfig;
import net.roomenn.eccore.utils.CameraAnimation.CameraAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Camera.class)
@Environment(EnvType.CLIENT)
public abstract class CameraMixin implements ICameraMixin {

    @Shadow private float cameraY;
    @Shadow private float lastCameraY;

    @Unique
    public ArrayList<CameraAnimation> animations = new ArrayList<CameraAnimation>();
    @Unique
    public ModConfig config = ECCore.config;

    @Inject(method = "update", at = @At(value = "RETURN"))
    private void animationAddon(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (thirdPerson && !animations.isEmpty()) {
            CameraAnimation anim = animations.get(0);
            anim.decrementTick(tickDelta);
            this.invokeSetPos(
                    focusedEntity.getX(),
                    focusedEntity.getEyeY(),
                    focusedEntity.getZ());
            Vec3d vec = anim.move(focusedEntity.getYaw());

            this.invokeMoveBy(-this.invokeClipToSpace(vec.x), vec.y, vec.z);

            if (anim.ticksLeft == 0f && !anim.hold) {
                animations.remove(0);
            }
        } else if (thirdPerson && animations.isEmpty()) {
            this.invokeSetPos(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX()), MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, this.lastCameraY, this.cameraY), MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ()));
            this.invokeMoveBy(-this.invokeClipToSpace(config.thirdPersonOffsetX), config.thirdPersonOffsetY, config.thirdPersonOffsetZ);
        }
    }

    @Override
    public void addAnim(CameraAnimation anim) {
        animations.add(anim);
    }

    @Override
    public void skipAnim() {
        animations.remove(0);
    }
}
