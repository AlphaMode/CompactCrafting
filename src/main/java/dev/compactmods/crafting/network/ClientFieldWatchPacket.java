package dev.compactmods.crafting.network;

import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.client.ClientPacketHandler;
import dev.compactmods.crafting.field.MiniaturizationField;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ClientFieldWatchPacket implements S2CPacket {

    private final IMiniaturizationField field;
    private final CompoundTag clientData;

    public ClientFieldWatchPacket(IMiniaturizationField field) {
        this.field = field;
        this.clientData = field.clientData();
    }

    public ClientFieldWatchPacket(FriendlyByteBuf buf) {
        this.field = new MiniaturizationField();
        this.clientData = buf.readAnySizeNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(field.clientData());
    }

    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        ClientPacketHandler.handleFieldData(clientData);
    }
}
