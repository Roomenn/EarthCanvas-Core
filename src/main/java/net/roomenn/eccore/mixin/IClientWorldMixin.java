package net.roomenn.eccore.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.roomenn.eccore.block.ModBlocks;
import net.roomenn.eccore.utils.trigger.IClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public interface IClientWorldMixin extends IClientWorld {

    @Invoker("randomBlockDisplayTick")
    void invokeRandomBlockDisplayTick(int centerX, int centerY, int centerZ, int radius, Random random, @Nullable Block block, BlockPos.Mutable pos);
}
