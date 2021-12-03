package dev.compactmods.crafting;

import java.util.function.Supplier;
import dev.compactmods.crafting.api.catalyst.CatalystType;
import dev.compactmods.crafting.api.recipe.layers.RecipeLayerType;
import dev.compactmods.crafting.items.FieldProjectorItem;
import dev.compactmods.crafting.projector.FieldProjectorBlock;
import dev.compactmods.crafting.projector.FieldProjectorTile;
import dev.compactmods.crafting.proxies.block.MatchFieldProxyBlock;
import dev.compactmods.crafting.proxies.block.RescanFieldProxyBlock;
import dev.compactmods.crafting.proxies.data.MatchFieldProxyEntity;
import dev.compactmods.crafting.proxies.data.RescanFieldProxyEntity;
import dev.compactmods.crafting.proxies.item.FieldProxyItem;
import dev.compactmods.crafting.recipes.MiniaturizationRecipe;
import dev.compactmods.crafting.recipes.MiniaturizationRecipeSerializer;
import dev.compactmods.crafting.recipes.catalyst.ItemStackCatalystMatcher;
import dev.compactmods.crafting.recipes.catalyst.ItemTagCatalystMatcher;
import dev.compactmods.crafting.recipes.layers.FilledComponentRecipeLayer;
import dev.compactmods.crafting.recipes.layers.HollowComponentRecipeLayer;
import dev.compactmods.crafting.recipes.layers.MixedComponentRecipeLayer;
import dev.compactmods.crafting.recipes.layers.SimpleRecipeLayerType;
import dev.compactmods.crafting.recipes.setup.BaseRecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = CompactCrafting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    // ================================================================================================================
    //   REGISTRIES
    // ================================================================================================================


    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CompactCrafting.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompactCrafting.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CompactCrafting.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CompactCrafting.MOD_ID);

    public static DeferredRegister<RecipeLayerType<?>> RECIPE_LAYERS = DeferredRegister.create((Class) RecipeLayerType.class, CompactCrafting.MOD_ID);
    public static IForgeRegistry<RecipeLayerType<?>> RECIPE_LAYER_TYPES;

    public static DeferredRegister<CatalystType<?>> CATALYSTS = DeferredRegister.create((Class) CatalystType.class, CompactCrafting.MOD_ID);
    public static IForgeRegistry<CatalystType<?>> CATALYST_TYPES;

    static {
        RECIPE_LAYERS.makeRegistry("recipe_layers", () -> new RegistryBuilder<RecipeLayerType<?>>()
                .tagFolder("recipe_layers"));

        CATALYSTS.makeRegistry("catalyst_types", () -> new RegistryBuilder<CatalystType<?>>()
                .tagFolder("catalyst_types"));
    }

    // ================================================================================================================
    // region  PROPERTIES
    // ================================================================================================================
    static final Supplier<BlockBehaviour.Properties> PROXY_PROPS = () -> BlockBehaviour.Properties.of(Material.HEAVY_METAL)
            .strength(8, 20)
            .requiresCorrectToolForDrops();

    static final Supplier<Item.Properties> BASE_ITEM_PROPS = () -> new Item.Properties().tab(CompactCrafting.ITEM_GROUP);

    // endregion ======================================================================================================

    // ================================================================================================================
    // region  BLOCKS
    // ================================================================================================================
    public static final RegistryObject<Block> FIELD_PROJECTOR_BLOCK = BLOCKS.register("field_projector", () ->
            new FieldProjectorBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(8, 20)
                    .isRedstoneConductor((state, level, pos) -> true)
                    .requiresCorrectToolForDrops()
            ));

    public static final RegistryObject<Block> RESCAN_FIELD_PROXY_BLOCK = BLOCKS.register("rescan_proxy", () ->
            new RescanFieldProxyBlock(PROXY_PROPS.get()));

    public static final RegistryObject<Block> MATCH_FIELD_PROXY_BLOCK = BLOCKS.register("match_proxy", () ->
            new MatchFieldProxyBlock(PROXY_PROPS.get()));

    // endregion ======================================================================================================

    // ================================================================================================================
    // region ITEMS
    // ================================================================================================================
    public static final RegistryObject<Item> FIELD_PROJECTOR_ITEM = ITEMS.register("field_projector", () ->
            new FieldProjectorItem(FIELD_PROJECTOR_BLOCK.get(), BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> PROJECTOR_DISH_ITEM = ITEMS.register("projector_dish", () ->
            new Item(BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> BASE_ITEM = ITEMS.register("base", () ->
            new Item(BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> RESCAN_PROXY_ITEM = ITEMS.register("rescan_proxy", () ->
            new FieldProxyItem(RESCAN_FIELD_PROXY_BLOCK.get(), BASE_ITEM_PROPS.get()));

    public static final RegistryObject<Item> MATCH_PROXY_ITEM = ITEMS.register("match_proxy", () ->
                new FieldProxyItem(MATCH_FIELD_PROXY_BLOCK.get(), BASE_ITEM_PROPS.get()));

    // endregion ======================================================================================================

    // ================================================================================================================
    // region  TILE ENTITIES
    // ================================================================================================================
    public static final RegistryObject<BlockEntityType<FieldProjectorTile>> FIELD_PROJECTOR_TILE = TILE_ENTITIES.register("field_projector", () ->
            BlockEntityType.Builder
                    .of(FieldProjectorTile::new, FIELD_PROJECTOR_BLOCK.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<RescanFieldProxyEntity>> RESCAN_PROXY_ENTITY = TILE_ENTITIES.register("rescan_proxy", () ->
            BlockEntityType.Builder
                    .of(RescanFieldProxyEntity::new, RESCAN_FIELD_PROXY_BLOCK.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<MatchFieldProxyEntity>> MATCH_PROXY_ENTITY = TILE_ENTITIES.register("match_proxy", () ->
            BlockEntityType.Builder
                    .of(MatchFieldProxyEntity::new, MATCH_FIELD_PROXY_BLOCK.get())
                    .build(null));

    // endregion ======================================================================================================

    // ================================================================================================================
    // region  MINIATURIZATION RECIPES
    // ================================================================================================================
    public static final RegistryObject<RecipeSerializer<MiniaturizationRecipe>> MINIATURIZATION_SERIALIZER = RECIPES.register("miniaturization", MiniaturizationRecipeSerializer::new);

    public static final ResourceLocation MINIATURIZATION_RECIPE_TYPE_ID = new ResourceLocation(CompactCrafting.MOD_ID, "miniaturization_recipe");

    public static final BaseRecipeType<MiniaturizationRecipe> MINIATURIZATION_RECIPE_TYPE = new BaseRecipeType<>(MINIATURIZATION_RECIPE_TYPE_ID);

    // endregion ======================================================================================================

    // ================================================================================================================
    // region  RECIPE LAYER SERIALIZERS
    // ================================================================================================================
    public static final RegistryObject<RecipeLayerType<FilledComponentRecipeLayer>> FILLED_LAYER_SERIALIZER =
            RECIPE_LAYERS.register("filled", () -> new SimpleRecipeLayerType<>(FilledComponentRecipeLayer.CODEC));

    public static final RegistryObject<RecipeLayerType<HollowComponentRecipeLayer>> HOLLOW_LAYER_TYPE =
            RECIPE_LAYERS.register("hollow", () -> new SimpleRecipeLayerType<>(HollowComponentRecipeLayer.CODEC));

    public static final RegistryObject<RecipeLayerType<MixedComponentRecipeLayer>> MIXED_LAYER_TYPE =
            RECIPE_LAYERS.register("mixed", () -> new SimpleRecipeLayerType<>(MixedComponentRecipeLayer.CODEC));

    // endregion ======================================================================================================

    // ================================================================================================================
    // region  CATALYST TYPES
    // ================================================================================================================
    public static final RegistryObject<CatalystType<ItemStackCatalystMatcher>> ITEM_STACK_CATALYST =
            CATALYSTS.register("item", ItemStackCatalystMatcher::new);

    public static final RegistryObject<CatalystType<ItemTagCatalystMatcher>> TAGGED_ITEM_CATALYST =
            CATALYSTS.register("tag", ItemTagCatalystMatcher::new);

    // endregion ======================================================================================================

    // region =========================================================================================================

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
        RECIPES.register(eventBus);

        // Recipe Types (Forge Registry setup does not call this yet)
        MINIATURIZATION_RECIPE_TYPE.register();

        RECIPE_LAYERS.register(eventBus);
        CATALYSTS.register(eventBus);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void layerRegistration(RegistryEvent.Register<RecipeLayerType<?>> evt) {
        RECIPE_LAYER_TYPES = evt.getRegistry();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void catalystRegistration(RegistryEvent.Register<CatalystType<?>> evt) {
        CATALYST_TYPES = evt.getRegistry();
    }
    // endregion ======================================================================================================
}
