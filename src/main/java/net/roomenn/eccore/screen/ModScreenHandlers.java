package net.roomenn.eccore.screen;

import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.roomenn.eccore.block.ModBlocks;

public class ModScreenHandlers {
    public static ScreenHandlerType<CutSceneMonitorGuiDescription> CUTSCENE_SCREEN_HANDLER_TYPE;
    public static ScreenHandlerType<RedstoneMonitorGuiDescription> REDSTONE_MONITOR_SCREEN_HANDLER_TYPE;

    public static void registerAllScreenHandlers() {
        CUTSCENE_SCREEN_HANDLER_TYPE = Registry.register(Registry.SCREEN_HANDLER,
                new Identifier(ModBlocks.CUTSCENE_MONITOR.getTranslationKey()),
                new ScreenHandlerType<>((syncId, inventory) ->
                        new CutSceneMonitorGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY)));
        REDSTONE_MONITOR_SCREEN_HANDLER_TYPE = Registry.register(Registry.SCREEN_HANDLER,
                new Identifier(ModBlocks.REDSTONE_MONITOR.getTranslationKey()),
                new ScreenHandlerType<>(RedstoneMonitorGuiDescription::new));
    }
}
