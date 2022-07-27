package net.roomenn.eccore.item;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.roomenn.eccore.ECCore;

public class ModItems {

    public static final Item TEST_BOW = registerItem("test_bow",
            new TestBowItem(new FabricItemSettings().group(ItemGroup.COMBAT).maxCount(1)));

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(ECCore.MOD_ID, name), item);
    }

    //Initializer
    public static void registerModItems() {}
}