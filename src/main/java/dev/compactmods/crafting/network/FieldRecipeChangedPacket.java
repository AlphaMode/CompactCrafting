package dev.compactmods.crafting.network;

import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.api.recipe.IMiniaturizationRecipe;
import dev.compactmods.crafting.client.ClientPacketHandler;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class FieldRecipeChangedPacket implements S2CPacket {

    private final BlockPos fieldCenter;

    @Nullable
    private final ResourceLocation recipe;

    public FieldRecipeChangedPacket(IMiniaturizationField field) {
        this.fieldCenter = field.getCenter();
        this.recipe = field.getCurrentRecipe().map(IMiniaturizationRecipe::getRecipeIdentifier).orElse(null);
    }

    public FieldRecipeChangedPacket(FriendlyByteBuf buf) {
        this.fieldCenter = buf.readBlockPos();
        if(buf.readBoolean())
            this.recipe = ResourceLocation.tryParse(buf.readUtf());
        else
            this.recipe = null;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(fieldCenter);
        buf.writeBoolean(recipe != null);
        if(recipe != null)
            buf.writeUtf(recipe.toString());
    }

    @Override
    public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientPacketHandler.handleRecipeChanged(fieldCenter, recipe));
    }
}
