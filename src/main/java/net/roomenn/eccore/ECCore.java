package net.roomenn.eccore;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.roomenn.eccore.api.ModConfig;
import net.roomenn.eccore.item.ModItems;
import net.roomenn.eccore.utils.Constants;
import net.roomenn.eccore.utils.ModModelPredicateProvider;
import net.roomenn.eccore.utils.Packets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ECCore implements ModInitializer {
	public static final String MOD_ID = "eccore";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ModConfig config;

	@Override
	public void onInitialize() {
		AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		Constants.registerConstants();
		ModItems.registerModItems();
		ModModelPredicateProvider.registerModModels();
		Packets.registerPacket();
	}
}
