package dev.compactmods.crafting.network;

import dev.compactmods.crafting.CompactCrafting;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel MAIN_CHANNEL = new SimpleChannel(
            new ResourceLocation(CompactCrafting.MOD_ID, "main")
    );

    public static void initialize() {
        MAIN_CHANNEL.registerS2CPacket(FieldActivatedPacket.class, 1);

        MAIN_CHANNEL.registerS2CPacket(FieldDeactivatedPacket.class, 2);

        MAIN_CHANNEL.registerS2CPacket(ClientFieldWatchPacket.class, 3);

        MAIN_CHANNEL.registerS2CPacket(ClientFieldUnwatchPacket.class, 4);

        MAIN_CHANNEL.registerS2CPacket(FieldRecipeChangedPacket.class, 5);
    }
}
