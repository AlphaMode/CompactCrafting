package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.projector.FieldProjectorBlock;
import dev.compactmods.crafting.projector.FieldProjectorEntity;
import dev.compactmods.crafting.proxies.block.MatchFieldProxyBlock;
import dev.compactmods.crafting.proxies.block.RescanFieldProxyBlock;
import dev.compactmods.crafting.proxies.data.MatchFieldProxyEntity;
import dev.compactmods.crafting.proxies.data.RescanFieldProxyEntity;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class CCBlocks {

    private static final LazyRegistrar<Block> BLOCKS = LazyRegistrar.create(Registry.BLOCK, CompactCrafting.MOD_ID);
    private static final LazyRegistrar<BlockEntityType<?>> TILE_ENTITIES = LazyRegistrar.create(Registry.BLOCK_ENTITY_TYPE, CompactCrafting.MOD_ID);

    public static final RegistryObject<Block> FIELD_PROJECTOR_BLOCK = BLOCKS.register("field_projector", () ->
            new FieldProjectorBlock(BlockBehaviour.Properties.of(Material.METAL)
                    .strength(8, 20)
                    .isRedstoneConductor((state, level, pos) -> true)
                    .requiresCorrectToolForDrops()
            ));

    static final Supplier<BlockBehaviour.Properties> PROXY_PROPS = () -> BlockBehaviour.Properties.of(Material.HEAVY_METAL)
            .strength(8, 20)
            .requiresCorrectToolForDrops();

    public static final RegistryObject<Block> RESCAN_FIELD_PROXY_BLOCK = BLOCKS.register("rescan_proxy", () ->
            new RescanFieldProxyBlock(PROXY_PROPS.get()));

    public static final RegistryObject<Block> MATCH_FIELD_PROXY_BLOCK = BLOCKS.register("match_proxy", () ->
            new MatchFieldProxyBlock(PROXY_PROPS.get()));

    public static final RegistryObject<BlockEntityType<FieldProjectorEntity>> FIELD_PROJECTOR_TILE = TILE_ENTITIES.register("field_projector", () ->
            BlockEntityType.Builder
                    .of(FieldProjectorEntity::new, FIELD_PROJECTOR_BLOCK.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<RescanFieldProxyEntity>> RESCAN_PROXY_ENTITY = TILE_ENTITIES.register("rescan_proxy", () ->
            BlockEntityType.Builder
                    .of(RescanFieldProxyEntity::new, RESCAN_FIELD_PROXY_BLOCK.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<MatchFieldProxyEntity>> MATCH_PROXY_ENTITY = TILE_ENTITIES.register("match_proxy", () ->
            BlockEntityType.Builder
                    .of(MatchFieldProxyEntity::new, MATCH_FIELD_PROXY_BLOCK.get())
                    .build(null));

    public static void init() {
        BLOCKS.register();
        TILE_ENTITIES.register();
    }
}
