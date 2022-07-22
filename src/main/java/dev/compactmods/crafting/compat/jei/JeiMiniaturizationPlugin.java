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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

public class JeiMiniaturizationPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new JeiMiniaturizationCraftingCategory());

        registry.addWorkstations(JeiMiniaturizationCraftingCategory.UID, EntryStacks.of(new ItemStack(CCItems.FIELD_PROJECTOR_ITEM.get(), 4)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(MiniaturizationRecipe.class, JeiMiniaturizationCraftingDisplay::new);
    }
}
