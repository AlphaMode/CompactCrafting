package dev.compactmods.crafting.recipes.components;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.components.RecipeComponentType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class ComponentRegistration {

    public static final ResourceLocation RECIPE_COMPONENTS_ID = new ResourceLocation(CompactCrafting.MOD_ID, "recipe_components");

    public static Registry<RecipeComponentType> COMPONENTS = FabricRegistryBuilder.createSimple(RecipeComponentType.class, RECIPE_COMPONENTS_ID).buildAndRegister();

    @SuppressWarnings("unchecked")
    public static LazyRegistrar<RecipeComponentType> RECIPE_COMPONENTS = LazyRegistrar.create(COMPONENTS, CompactCrafting.MOD_ID);

    // ================================================================================================================
    //   RECIPE COMPONENTS
    // ================================================================================================================
    public static final RegistryObject<RecipeComponentType<BlockComponent>> BLOCK_COMPONENT =
            RECIPE_COMPONENTS.register("block", () -> new SimpleRecipeComponentType<>(BlockComponent.CODEC));

    public static final RegistryObject<RecipeComponentType<EmptyBlockComponent>> EMPTY_BLOCK_COMPONENT =
            RECIPE_COMPONENTS.register("empty", () -> new SimpleRecipeComponentType<>(EmptyBlockComponent.CODEC));

    // ================================================================================================================
    //   INITIALIZATION
    // ================================================================================================================
    @SuppressWarnings("unchecked")
    public static <T> Class<T> c(Class<?> cls) {
        return (Class<T>) cls;
    }

    public static void init() {
        RECIPE_COMPONENTS.register();
    }
}
