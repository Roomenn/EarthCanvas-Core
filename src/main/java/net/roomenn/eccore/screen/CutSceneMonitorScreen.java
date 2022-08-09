package net.roomenn.eccore.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CutSceneMonitorScreen extends CottonInventoryScreen<CutSceneMonitorGuiDescription> {
    public CutSceneMonitorScreen(CutSceneMonitorGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
