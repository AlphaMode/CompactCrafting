package dev.compactmods.crafting.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.compactmods.crafting.api.field.MiniaturizationFieldSize;
import dev.compactmods.crafting.client.ClientPacketHandler;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class FieldDeactivatedPacket implements S2CPacket {

    protected static final Codec<FieldDeactivatedPacket> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.xmap(MiniaturizationFieldSize::valueOf, Enum::name)
                    .fieldOf("size").forGetter(x -> x.fieldSize),
            BlockPos.CODEC.fieldOf("center").forGetter(x -> x.fieldCenter)
    ).apply(i, FieldDeactivatedPacket::new));

    private final MiniaturizationFieldSize fieldSize;
    private final BlockPos fieldCenter;
    private final BlockPos[] projectors;

    public FieldDeactivatedPacket(MiniaturizationFieldSize fieldSize, BlockPos fieldCenter) {
        this.fieldSize = fieldSize;
        this.fieldCenter = fieldCenter;

        this.projectors = fieldSize.getProjectorLocations(fieldCenter)
                .map(BlockPos::immutable).toArray(BlockPos[]::new);
    }

    public FieldDeactivatedPacket(FriendlyByteBuf buf) {
        FieldDeactivatedPacket pkt = buf.readWithCodec(CODEC);

        this.fieldSize = pkt.fieldSize;
        this.fieldCenter = pkt.fieldCenter;

        this.projectors = fieldSize.getProjectorLocations(fieldCenter)
                .map(BlockPos::immutable).toArray(BlockPos[]::new);
    }

    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        ClientPacketHandler.handleFieldDeactivation(fieldCenter);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeWithCodec(CODEC, this);
    }
}
