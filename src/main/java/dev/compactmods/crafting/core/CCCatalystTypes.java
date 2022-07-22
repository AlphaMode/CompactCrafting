package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.catalyst.CatalystType;
import dev.compactmods.crafting.recipes.catalyst.ItemStackCatalystMatcher;
import dev.compactmods.crafting.recipes.catalyst.ItemTagCatalystMatcher;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unchecked")
public class CCCatalystTypes {

    public static final ResourceLocation CATALYSTS_RL = new ResourceLocation(CompactCrafting.MOD_ID, "catalyst_types");

    public static MappedRegistry<CatalystType> CATALYST_TYPES = FabricRegistryBuilder.createSimple(CatalystType.class, CATALYSTS_RL).buildAndRegister();

    public static LazyRegistrar<CatalystType> CATALYSTS = LazyRegistrar.create(CATALYST_TYPES, CompactCrafting.MOD_ID);

    // ================================================================================================================

    public static final RegistryObject<CatalystType<ItemStackCatalystMatcher>> ITEM_STACK_CATALYST =
            CATALYSTS.register("item", new ItemStackCatalystMatcher());

    public static final RegistryObject<CatalystType<ItemTagCatalystMatcher>> TAGGED_ITEM_CATALYST =
            CATALYSTS.register("tag", new ItemTagCatalystMatcher());

    public static void init() {
        CATALYSTS.register();
    }

    // ================================================================================================================
}
