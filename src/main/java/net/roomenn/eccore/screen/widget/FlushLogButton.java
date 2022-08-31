package net.roomenn.eccore.screen.widget;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.screen.RedstoneMonitorGuiDescription;

public class FlushLogButton extends WButton {
    private final Identifier text;

    public FlushLogButton() {
        text = new Identifier("gui." + ECCore.MOD_ID, "monitor.flush_logs");

        setIcon(new ItemIcon(Items.PAPER));
        setOnClick(() -> {
            setEnabled(false);
            if (host != null) {
                ((RedstoneMonitorGuiDescription) host).flushLogs();
            }
        });
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        tooltip.add(Text.translatable(text.toTranslationKey()));
    }
}
