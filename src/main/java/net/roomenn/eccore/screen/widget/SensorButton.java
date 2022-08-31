package net.roomenn.eccore.screen.widget;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.screen.RedstoneMonitorGuiDescription;

public class SensorButton extends WButton {
    private final Identifier text;

    public SensorButton() {
        text = new Identifier("gui." + ECCore.MOD_ID, "monitor.is_sensor");

        setIcon(new ItemIcon(Items.LEVER));
        setOnClick(() -> {
            if (host != null) {
                ((RedstoneMonitorGuiDescription) host).setIsSensor(isEnabled());
            }
            setIcon(new ItemIcon(isEnabled() ? Items.REDSTONE_TORCH : Items.LEVER));
            setEnabled(!isEnabled());

        });
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        tooltip.add(Text.translatable(text.toTranslationKey()));
    }

    public void init(boolean isSensor) {
        if (isSensor) {
            setEnabled(false);
            setIcon(new ItemIcon(Items.REDSTONE_TORCH));
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public InputResult onClick(int x, int y, int button) {
        if (isWithinBounds(x, y)) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            if (getOnClick() !=null) getOnClick().run();
            return InputResult.PROCESSED;
        }
        return InputResult.IGNORED;
    }
}
