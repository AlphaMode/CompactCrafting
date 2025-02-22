package dev.compactmods.crafting.recipes.layers;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.compactmods.crafting.api.recipe.layers.RecipeLayerType;
import dev.compactmods.crafting.core.CCLayerTypes;
import net.minecraft.resources.ResourceLocation;

public final class RecipeLayerTypeCodec implements Codec<RecipeLayerType<?>> {
    // Lifted and modified from a Forge PR #7668, temporary until Forge itself supports the Codec interface
    public static final Codec<RecipeLayerType<?>> INSTANCE = new RecipeLayerTypeCodec();

    private RecipeLayerTypeCodec() {}

    public static <T> DataResult<Pair<RecipeLayerType<?>, T>> handleDecodeResult(Pair<ResourceLocation, T> keyValuePair) {
        ResourceLocation id = keyValuePair.getFirst();
        if(!CCLayerTypes.RECIPE_LAYER_TYPES.containsKey(id))
            return DataResult.error("Unknown registry key: " + id);

        return DataResult.success(keyValuePair.mapFirst(CCLayerTypes.RECIPE_LAYER_TYPES::get));
    }

    @Override
    public <T> DataResult<Pair<RecipeLayerType<?>, T>> decode(DynamicOps<T> ops, T input) {
        return ResourceLocation.CODEC.decode(ops, input).flatMap(RecipeLayerTypeCodec::handleDecodeResult);
    }

    @Override
    public <T> DataResult<T> encode(RecipeLayerType<?> input, DynamicOps<T> ops, T prefix) {
        ResourceLocation key = CCLayerTypes.RECIPE_LAYER_TYPES.getKey(input);
        if(key == null)
            return DataResult.error("Unknown registry element " + input);

        T toMerge = ops.createString(key.toString());
        return ops.mergeToPrimitive(prefix, toMerge);
    }
}
