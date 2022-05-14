package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import dev.compactmods.crafting.recipes.MiniaturizationRecipeSerializer;
import dev.compactmods.crafting.recipes.setup.BaseRecipeType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;


public class CCMiniaturizationRecipes {

    private static final LazyRegistrar<RecipeSerializer<?>> RECIPES = LazyRegistrar.create(Registry.RECIPE_SERIALIZER, CompactCrafting.MOD_ID);

    // ================================================================================================================

    public static final RegistryObject<RecipeSerializer<MiniaturizationRecipe>> MINIATURIZATION_SERIALIZER = RECIPES.register("miniaturization", MiniaturizationRecipeSerializer::new);

    public static final ResourceLocation MINIATURIZATION_RECIPE_TYPE_ID = new ResourceLocation(CompactCrafting.MOD_ID, "miniaturization_recipe");

    public static final BaseRecipeType<MiniaturizationRecipe> MINIATURIZATION_RECIPE_TYPE = new BaseRecipeType<>(MINIATURIZATION_RECIPE_TYPE_ID);

    // ================================================================================================================

    public static void init() {
        RECIPES.register();
        MINIATURIZATION_RECIPE_TYPE.register();
    }
}
