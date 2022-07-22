package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.recipe.layers.RecipeLayerType;
import dev.compactmods.crafting.recipes.layers.FilledComponentRecipeLayer;
import dev.compactmods.crafting.recipes.layers.HollowComponentRecipeLayer;
import dev.compactmods.crafting.recipes.layers.MixedComponentRecipeLayer;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class CCLayerTypes {

    public static final ResourceLocation REC_LAYERS = new ResourceLocation(CompactCrafting.MOD_ID, "recipe_layers");

    public static Registry<RecipeLayerType> RECIPE_LAYER_TYPES = FabricRegistryBuilder.createSimple(RecipeLayerType.class, REC_LAYERS).buildAndRegister();

    public static LazyRegistrar<RecipeLayerType> RECIPE_LAYERS = LazyRegistrar.create(RECIPE_LAYER_TYPES, CompactCrafting.MOD_ID);

    // ================================================================================================================
    // region  RECIPE LAYER SERIALIZERS
    // ================================================================================================================
    public static final RegistryObject<RecipeLayerType<FilledComponentRecipeLayer>> FILLED_LAYER_SERIALIZER =
            RECIPE_LAYERS.register("filled", new FilledComponentRecipeLayer());

    public static final RegistryObject<RecipeLayerType<HollowComponentRecipeLayer>> HOLLOW_LAYER_TYPE =
            RECIPE_LAYERS.register("hollow", new HollowComponentRecipeLayer());

    public static final RegistryObject<RecipeLayerType<MixedComponentRecipeLayer>> MIXED_LAYER_TYPE =
            RECIPE_LAYERS.register("mixed", new MixedComponentRecipeLayer());

    // endregion ======================================================================================================

    public static void init() {
        RECIPE_LAYERS.register();
    }
}
