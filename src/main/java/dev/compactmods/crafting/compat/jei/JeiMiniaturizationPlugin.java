package dev.compactmods.crafting.compat.jei;

import java.util.List;
import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.core.CCItems;
import dev.compactmods.crafting.core.CCMiniaturizationRecipes;
import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import dev.compactmods.crafting.recipes.setup.RecipeBase;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

public class JeiMiniaturizationPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new JeiMiniaturizationCraftingCategory(registration.getJeiHelpers().getGuiHelper()));

        registry.addWorkstations(JeiMiniaturizationCraftingCategory.UID, EntryStacks.of(new ItemStack(CCItems.FIELD_PROJECTOR_ITEM.get(), 4)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(MiniaturizationRecipe.class, JeiMiniaturizationCraftingDisplay::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel w = Minecraft.getInstance().level;
        RecipeManager rm = w == null ? null : w.getRecipeManager();
        if(rm != null) {
            List<RecipeBase> miniRecipes = rm.getAllRecipesFor(CCMiniaturizationRecipes.MINIATURIZATION_RECIPE_TYPE);
            registration.addRecipes(miniRecipes, JeiMiniaturizationCraftingCategory.UID);
        }
    }
}
