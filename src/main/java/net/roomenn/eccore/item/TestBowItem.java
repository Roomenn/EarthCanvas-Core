package net.roomenn.eccore.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.api.ModConfig;
import net.roomenn.eccore.utils.CameraAnimation.CameraAnimation;

public class TestBowItem extends BowItem {
    public TestBowItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));

        ModConfig config = ECCore.config;
        CameraAnimation.animationPacket((ServerPlayerEntity) user, config.camAnimX, config.camAnimY, config.camAnimZ,3, config.transition, true);

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        if (world.isClient) return;

        ModConfig config = ECCore.config;
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i);

        if (((double)f > 0.9D)) {
            CameraAnimation.animationPacket((ServerPlayerEntity) user, 3,0,-0.3f,0.5f,"ease-out",false);
        }
        CameraAnimation.animationPacket((ServerPlayerEntity) user, config.thirdPersonOffsetX, config.thirdPersonOffsetY, config.thirdPersonOffsetZ,1.0f,"ease-in",false);
        CameraAnimation.animationSkipPacket((ServerPlayerEntity) user);
    }
}
