package dev.compactmods.crafting.network;

import dev.compactmods.crafting.client.ClientPacketHandler;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ClientFieldUnwatchPacket implements S2CPacket {

    private final BlockPos center;

    public ClientFieldUnwatchPacket(BlockPos center) {
        this.center = center;
    }

    public ClientFieldUnwatchPacket(FriendlyByteBuf buf) {
        this.center = buf.readBlockPos();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(center);
    }

    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientPacketHandler.removeField(center));
    }
}
