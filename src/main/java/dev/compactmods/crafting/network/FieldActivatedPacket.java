package dev.compactmods.crafting.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.api.field.MiniaturizationFieldSize;
import dev.compactmods.crafting.client.ClientPacketHandler;
import dev.compactmods.crafting.field.MiniaturizationField;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;

public class FieldActivatedPacket implements S2CPacket {

    private IMiniaturizationField field;

    @Nullable
    protected CompoundTag clientData;

    protected static final Codec<FieldActivatedPacket> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.xmap(MiniaturizationFieldSize::valueOf, Enum::name)
                    .fieldOf("size").forGetter(x -> x.field.getFieldSize()),

            BlockPos.CODEC.fieldOf("center").forGetter(x -> x.field.getCenter()),

            CompoundTag.CODEC.fieldOf("clientData").forGetter(x -> x.clientData)
    ).apply(i, FieldActivatedPacket::new));

    public FieldActivatedPacket(IMiniaturizationField field) {
        this.field = field;
        this.clientData = field.clientData();
    }

    private FieldActivatedPacket(MiniaturizationFieldSize fieldSize, BlockPos center, CompoundTag clientData) {
        this.field = new MiniaturizationField();
        field.setSize(fieldSize);
        field.setCenter(center);
        this.clientData = clientData;
    }

    public FieldActivatedPacket(FriendlyByteBuf buf) {
        FieldActivatedPacket base = buf.readWithCodec(CODEC);
        this.field = base.field;
        this.clientData = base.clientData;
    }

    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> {
            ClientPacketHandler.handleFieldActivation(field, clientData);
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeWithCodec(CODEC, this);
    }
}
