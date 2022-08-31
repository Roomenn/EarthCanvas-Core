package net.roomenn.eccore.screen.widget;

import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.Item;
import net.minecraft.screen.Property;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.screen.RedstoneMonitorGuiDescription;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipButton extends WButton {
    private static final Identifier DARK_WIDGETS_LOCATION = new Identifier(ECCore.MOD_ID, "textures/widget/dark_widgets.png");
    public static final Identifier WIDGETS_TEXTURE = new Identifier(ECCore.MOD_ID, "textures/gui/widgets.png");
    private final Identifier text;
    private Property property;
    private List<WWidget> list;
    private Integer value;

    /**
     * Boolean constructors:
     *  set to 1 or 0 to its property when clicked
     */
    public TooltipButton(String identifier, Item icon) {
        this(identifier);
        setIcon(new ItemIcon(icon));
    }

    public TooltipButton(String identifier, String texture) {
        this(identifier);
        setIcon(new TextureIcon(new Identifier(texture)));
    }

    private TooltipButton(String identifier) {
        text = new Identifier("gui." + ECCore.MOD_ID, identifier);

        Runnable onClickRunnable = () -> {
            if (!isEnabled()) {
                if (list != null) return;
                property.set(0);
                setEnabled(true);
                saveData();
            } else {
                if (list != null) {
                    List<WWidget> buttonList = list.stream().filter(w ->
                            w instanceof TooltipButton b && !b.isEnabled()
                    ).toList();
                    for (WWidget button : buttonList) {
                        ((TooltipButton) button).property.set(0);
                        ((TooltipButton) button).setEnabled(true);
                    }
                }
                property.set(1);
                setEnabled(false);
                saveData();
            }
        };
        setOnClick(onClickRunnable);
    }

    /**
     * Value constructors:
     *  set its property to value when pressed
     */
    public TooltipButton(String identifier, Item icon, int value) {
        this(identifier, value);
        setIcon(new ItemIcon(icon));
    }

    public TooltipButton(String identifier, String texture, int value) {
        this(identifier, value);
        setIcon(new TextureIcon(new Identifier(texture)));
    }

    private TooltipButton(String identifier, int value) {
        text = new Identifier("gui." + ECCore.MOD_ID, identifier);
        this.value = value;

        Runnable onClickRunnable = () -> {
            if (!isEnabled()) {
                if (list != null) return;
                setEnabled(true);
            } else {
                if (list != null) {
                    List<WWidget> buttonList = list.stream().filter(w ->
                            w instanceof TooltipButton b && !b.isEnabled()
                    ).toList();
                    for (WWidget button : buttonList) {
                        ((TooltipButton) button).setEnabled(true);
                    }
                }
                property.set(value);
                setEnabled(false);
                saveData();
            }
        };
        setOnClick(onClickRunnable);
    }

    @Override
    public void addTooltip(TooltipBuilder tooltip) {
        tooltip.add(Text.translatable(text.toTranslationKey()));
    }

    public void init(Property property, @Nullable List<WWidget> list) {
        this.property = property;
        this.list = list;

        if (value != null) setEnabled(value != property.get());
        else setEnabled(property.get() == 0);
    }

    @Environment(EnvType.CLIENT)
    private void saveData(){
        if (host != null) {
            ((RedstoneMonitorGuiDescription) host).saveData();
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

    @Environment(EnvType.CLIENT)
    static Identifier getTexture() {
        return LibGui.isDarkMode() ? DARK_WIDGETS_LOCATION : WIDGETS_TEXTURE;
    }

}
