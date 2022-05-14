package dev.compactmods.crafting.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;

public class CodecExtensions {

     public static final Codec<Block> BLOCK_ID_CODEC = ResourceLocation.CODEC
             .flatXmap(rl -> Registry.BLOCK.containsKey(rl) ?
                             DataResult.success(Registry.BLOCK.get(rl)) :
                             DataResult.error(String.format("Block %s is not registered.", rl)),
                bl -> DataResult.success(Registry.BLOCK.getKey(bl)))
             .stable();
}
