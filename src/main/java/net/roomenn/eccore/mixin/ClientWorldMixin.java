package net.roomenn.eccore.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.roomenn.eccore.block.ModBlocks;
import net.roomenn.eccore.item.trigger.TriggerBlockItem;
import net.roomenn.eccore.item.trigger.TriggerWandItem;
import net.roomenn.eccore.utils.trigger.TriggerBlocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public abstract class ClientWorldMixin implements IClientWorldMixin {

    @Final
    @Shadow
    private final MinecraftClient client = MinecraftClient.getInstance();

    private static final Set<Block> TRIGGER_BLOCKS;

    static {
        TRIGGER_BLOCKS = TriggerBlocks.getTriggerBlocks();
    }

    @Override
    public boolean getTriggerParticules() {
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
            ItemStack itemStack = this.client.player.getMainHandStack();
            Item item = itemStack.getItem();
            return (item instanceof TriggerBlockItem || item instanceof TriggerWandItem);
        }
        return false;
    }

    @Inject(method = "doRandomBlockDisplayTicks", at = @At(value = "RETURN"))
    private void animationAddon(int centerX, int centerY, int centerZ, CallbackInfo ci) {
        if (this.getTriggerParticules()) {
            Random random = Random.create();
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (Block block:TRIGGER_BLOCKS) {
                for(int j = 0; j < 667; ++j) {
                    this.invokeRandomBlockDisplayTick(centerX, centerY, centerZ, 16, random, block, mutable);
                    this.invokeRandomBlockDisplayTick(centerX, centerY, centerZ, 32, random, block, mutable);
                }
            }
        }



    }
}
