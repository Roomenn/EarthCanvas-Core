package net.roomenn.eccore.screen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class RedstoneMonitorScreen extends CottonInventoryScreen<RedstoneMonitorGuiDescription> {
    RedstoneMonitorGuiDescription guiDescription;

    public RedstoneMonitorScreen(RedstoneMonitorGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
        guiDescription = description;
    }
}
