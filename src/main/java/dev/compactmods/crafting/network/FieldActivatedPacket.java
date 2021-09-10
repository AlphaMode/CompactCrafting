package dev.compactmods.crafting.network;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.client.ClientPacketHandler;
import dev.compactmods.crafting.api.field.MiniaturizationFieldSize;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.function.Supplier;

public class FieldActivatedPacket extends FieldChangedPacket {

    public FieldActivatedPacket(MiniaturizationFieldSize fieldSize, BlockPos fieldCenter) {
        super(fieldSize, fieldCenter);
    }

    public static void handle(FieldActivatedPacket message, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();

        ctx.enqueueWork(() -> {
            DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
                ClientPacketHandler.handleFieldActivation(message.projectorLocations, message.fieldSize);
                return null;
            });
        });

        ctx.setPacketHandled(true);
    }

    public static void encode(FieldActivatedPacket pkt, PacketBuffer buf) {
        try {
            buf.writeWithCodec(CODEC, pkt);
        } catch (IOException e) {
            CompactCrafting.LOGGER.error(e);
        }
    }

    @Nullable
    public static FieldActivatedPacket decode(PacketBuffer buf) {
        try {
            FieldChangedPacket base = buf.readWithCodec(CODEC);
            return new FieldActivatedPacket(base.fieldSize, base.fieldCenter);
        } catch (IOException e) {
            CompactCrafting.LOGGER.error(e);
        }

        return null;
    }
}
