package net.roomenn.eccore.screen.widget;

import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.screen.Property;
import net.minecraft.text.Text;
import net.roomenn.eccore.ECCore;
import net.roomenn.eccore.screen.RedstoneMonitorGuiDescription;

import java.util.regex.Pattern;

public class IntTextField extends WTextField {
    Property property;

    public IntTextField(Text suggestion) {
        super(suggestion);
        setEditable(false);

        setTextPredicate(text -> Pattern.matches("\\d+", text));

        setChangedListener(x -> {
            property.set(Integer.parseInt(x));
            if (host != null) {
                ((RedstoneMonitorGuiDescription) host).saveData();
            }
        });
    }

    public void init(Property property) {
        this.property = property;
        if (property.get() != 0) setText(Integer.toString(property.get()));
        setEditable(true);
    }
}
