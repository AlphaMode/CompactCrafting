package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.items.FieldProjectorItem;
import dev.compactmods.crafting.proxies.item.FieldProxyItem;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CCItems {

    private static final LazyRegistrar<Item> ITEMS = LazyRegistrar.create(Registry.ITEM, CompactCrafting.MOD_ID);

    // ================================================================================================================

    static final Supplier<Item.Properties> BASE_ITEM_PROPS = () -> new Item.Properties().tab(CompactCrafting.ITEM_GROUP);

    public static final RegistryObject<Item> FIELD_PROJECTOR_ITEM = ITEMS.register("field_projector", () ->
            new FieldProjectorItem(CCBlocks.FIELD_PROJECTOR_BLOCK.get(), BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> PROJECTOR_DISH_ITEM = ITEMS.register("projector_dish", () ->
            new Item(BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> BASE_ITEM = ITEMS.register("base", () ->
            new Item(BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> RESCAN_PROXY_ITEM = ITEMS.register("rescan_proxy", () ->
            new FieldProxyItem(CCBlocks.RESCAN_FIELD_PROXY_BLOCK.get(), BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> MATCH_PROXY_ITEM = ITEMS.register("match_proxy", () ->
            new FieldProxyItem(CCBlocks.MATCH_FIELD_PROXY_BLOCK.get(), BASE_ITEM_PROPS.get()));

    // ================================================================================================================

    public static void init() {
        ITEMS.register();
    }
}
