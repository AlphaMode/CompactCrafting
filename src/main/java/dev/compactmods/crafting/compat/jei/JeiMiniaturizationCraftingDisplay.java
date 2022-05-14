package dev.compactmods.crafting.compat.jei;

import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;

public class JeiMiniaturizationCraftingDisplay extends BasicDisplay {

    private final MiniaturizationRecipe recipe;

    public JeiMiniaturizationCraftingDisplay(MiniaturizationRecipe recipe) {
        super(inputs, outputs);
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
