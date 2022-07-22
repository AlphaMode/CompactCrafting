package dev.compactmods.crafting.compat.jei;

import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Arrays;
import java.util.Collections;

public class JeiMiniaturizationCraftingDisplay extends BasicDisplay {

    private final MiniaturizationRecipe recipe;

    public JeiMiniaturizationCraftingDisplay(MiniaturizationRecipe recipe) {
        super(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.ofItemStacks(Arrays.asList(recipe.getOutputs()))));
        this.recipe = recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return JeiMiniaturizationCraftingCategory.UID;
    }

    public MiniaturizationRecipe getRecipe() {
        return recipe;
    }
}
