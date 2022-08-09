package net.roomenn.eccore.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.roomenn.eccore.screen.CutSceneMonitorGuiDescription;
import net.roomenn.eccore.screen.CutSceneMonitorScreen;
import net.roomenn.eccore.screen.ModScreenHandlers;

@Environment(EnvType.CLIENT)
public class ECCoreClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.<CutSceneMonitorGuiDescription, CutSceneMonitorScreen>register(ModScreenHandlers.CUTSCENE_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new CutSceneMonitorScreen(gui, inventory.player, title));
    }
}